package io.mosip.testrig.apirig.utils;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.log4j.Logger;

@ClientEndpoint
public class WebSocketClientUtil extends Endpoint {

    private static final Logger logger = Logger.getLogger(WebSocketClientUtil.class);
    private Session session;
    private CountDownLatch latch;
    private String cookie;
    private String subscribeDestination;
    private String sendDestination;
    
    
    // Global map to store received messages, keyed by message ID or custom key
    private static final Map<String, String> messageStore = new ConcurrentHashMap<>();

    public WebSocketClientUtil(String cookie, String subscribeDestination, String sendDestination) {
        this.cookie = cookie;
        this.subscribeDestination = subscribeDestination;
        this.sendDestination = sendDestination;
        latch = new CountDownLatch(1); // Initially, latch is set to 1
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        logger.info("WebSocket opened");

        // Add message handler
        session.addMessageHandler(String.class, this::onMessage);

        try {
            // Send CONNECT frame to initiate WebSocket handshake
            String connectFrame = "CONNECT\naccept-version:1.2\n\n\u0000";
            session.getBasicRemote().sendText(connectFrame);
            logger.info("Sent CONNECT frame: " + connectFrame);

            // Send SUBSCRIBE frame to subscribe to destination
            String subscribeFrame = String.format("SUBSCRIBE\nid:sub-0\ndestination:%s\n\n\u0000", subscribeDestination);
            session.getBasicRemote().sendText(subscribeFrame);
            logger.info("Sent SUBSCRIBE frame: " + subscribeFrame);

        } catch (Exception e) {
            logger.error("Error during connection setup", e);
            latch.countDown();
        }
    }

    public void connect(String uri) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        ClientEndpointConfig config = ClientEndpointConfig.Builder.create()
            .configurator(new ClientEndpointConfig.Configurator() {
                @Override
                public void beforeRequest(Map<String, java.util.List<String>> headers) {
                    headers.put("Cookie", Collections.singletonList(cookie));
                    logger.info("Request headers: " + headers);
                }
            }).build();

        try {
            logger.info("Attempting to connect to: " + uri);
            container.connectToServer(this, config, new URI(uri));

            logger.info("Successfully connected to the WebSocket server.");

        } catch (Exception e) {
            logger.error("Connection failed: ", e);
        }
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("Received message: " + message);

        // Store the received message in the global map (keyed by message ID or custom identifier)
        // Assuming the message contains a message-id field
        String messageId = extractMessageId(message);
        if (messageId != null) {
            messageStore.put(messageId, message);
            logger.info("Stored message with ID: " + messageId);
        } else {
            logger.warn("Received message without a valid message ID: " + message);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        if (closeReason != null) {
            logger.info("Connection closed: " + closeReason.getCloseCode() + " (" + closeReason.getReasonPhrase() + ")");
        } else {
            logger.info("Connection closed with no specific reason.");
        }
        latch.countDown();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("Error occurred: ", throwable);
        if (session != null && session.isOpen()) {
            try {
                // Attempting to reconnect if needed
                logger.info("Attempting to reconnect...");
                connect("wss://your-websocket-url");
            } catch (Exception e) {
                logger.error("Error reconnecting: ", e);
            }
        }
        latch.countDown();
    }


    public void sendMessage(String messageContent) {
        if (session != null && session.isOpen()) {
            try {
                String sendFrame = String.format("SEND\ndestination:%s\ncontent-type:application/json\n\n%s\u0000", sendDestination, messageContent);
                session.getBasicRemote().sendText(sendFrame);
                logger.info("Sent message: " + sendFrame);
            } catch (Exception e) {
                logger.error("Error sending message: ", e);
            }
        } else {
            logger.warn("Connection is not open. Unable to send message.");
            session = null;
        }
    }

    public void closeConnection() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
                logger.info("WebSocket connection closed.");
            }
        } catch (Exception e) {
            logger.error("Error closing connection", e);
        }
    }

    // Method to extract the message ID from the received message (this is a placeholder)
    private String extractMessageId(String message) {
        try {
            if (message.contains("message-id")) {
                String[] parts = message.split("message-id:");
                if (parts.length > 1) {
                    String messageId = parts[1].split("-")[0].trim(); // Adjust extraction logic as needed
                    return messageId;
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting message ID", e);
        }
        return null;
    }

    public static Map<String, String> getMessageStore() {
        return messageStore;
    }

    public Session getSession() {
        return session;
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}