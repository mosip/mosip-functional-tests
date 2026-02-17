package io.mosip.testrig.apirig.testrunner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.testrig.apirig.otp.Root;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.NotificationService;

public class OTPListener {

    private static final Logger logger = Logger.getLogger(OTPListener.class);

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static volatile boolean bTerminate = false;

    public OTPListener() {
        if (ConfigManager.IsDebugEnabled()) {
            logger.setLevel(Level.ALL);
        } else {
            logger.setLevel(Level.ERROR);
        }
    }

    // --------------------------------------------------
    // Start WebSocket Listener
    // --------------------------------------------------
    public void run() {
        try {
            String base = ConfigManager.getIAMUrl();

            if (base.contains("/auth")) {
                base = base.replace("/auth", "");
            }

            String websocketUrl =
                    "wss://smtp."
                            + base.substring(base.indexOf(".") + 1)
                            + "/mocksmtp/websocket";

            logger.info("Connecting to OTP WebSocket: " + websocketUrl);

            HTTP_CLIENT.newWebSocketBuilder()
                    .buildAsync(URI.create(websocketUrl), new WebSocketClient())
                    .join();

        } catch (Exception e) {
            logger.error("Failed to start OTP WebSocket listener", e);
        }
    }

    // --------------------------------------------------
    // WebSocket Client
    // --------------------------------------------------
    private static class WebSocketClient implements WebSocket.Listener {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void onOpen(WebSocket webSocket) {
            logger.info("OTP WebSocket connection opened.");
            Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket,
                                         CharSequence data,
                                         boolean last) {

            if (bTerminate) {
                logger.info("OTP Listener terminating...");
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE,
                        "Test Suite Completed");
                return Listener.super.onText(webSocket, data, last);
            }

            try {
                Root root = objectMapper.readValue(data.toString(), Root.class);

                String otpMessage = "";
                String notificationMessage = "";
                String address = "";

                if ("SMS".equalsIgnoreCase(root.type)) {

                    otpMessage = root.subject;
                    notificationMessage = root.subject;
                    address = root.to.text.trim();

                } else if ("MAIL".equalsIgnoreCase(root.type)) {

                    otpMessage = root.html;
                    notificationMessage = root.subject;
                    address = root.to.value.get(0).address;

                } else {
                    logger.warn("Unsupported notification type: " + root.type);
                    return Listener.super.onText(webSocket, data, last);
                }

                logger.info(String.format(
                        "[Thread:%s] Notification received for %s",
                        Thread.currentThread().getName(),
                        address));

                // 🔔 Store ALL notifications
                NotificationService.storeNotification(address,
                        notificationMessage);

                // 🔐 Store OTP if present
                if (!NotificationService.parseOtp(otpMessage).isEmpty()) {
                    NotificationService.storeOtp(address, otpMessage);
                }

            } catch (Exception e) {
                logger.error("Error processing WebSocket message", e);
            }

            return Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            logger.error("WebSocket error occurred", error);
            Listener.super.onError(webSocket, error);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket,
                                          int statusCode,
                                          String reason) {
            logger.warn("WebSocket closed. Status: "
                    + statusCode + " Reason: " + reason);
            return Listener.super.onClose(webSocket, statusCode, reason);
        }
    }
}