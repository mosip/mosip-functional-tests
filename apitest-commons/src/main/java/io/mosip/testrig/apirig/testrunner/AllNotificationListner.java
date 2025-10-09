package io.mosip.testrig.apirig.testrunner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.testrig.apirig.otp.Root;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.ConfigManager;

public class AllNotificationListner {
    private static Logger logger = Logger.getLogger(AllNotificationListner.class);

    public static Map<Object, Object> allNotificationMapS = Collections
            .synchronizedMap(new HashMap<Object, Object>());

    public static Boolean bTerminate = false;

    public AllNotificationListner() {
        if (ConfigManager.IsDebugEnabled())
            logger.setLevel(Level.ALL);
        else
            logger.setLevel(Level.ERROR);
    }

    public void run() {
        try {
            String a1 = "wss://smtp.";
            String externalurl = ConfigManager.getIAMUrl();

            if (externalurl.contains("/auth")) {
                externalurl = externalurl.replace("/auth", "");
            }
            String a2 = externalurl.substring(externalurl.indexOf(".") + 1);
            String a3 = "/mocksmtp/websocket";

            WebSocket ws = HttpClient.newHttpClient().newWebSocketBuilder()
                    .buildAsync(URI.create(a1 + a2 + a3), new WebSocketClient()).join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static class WebSocketClient implements WebSocket.Listener {

        public WebSocketClient() {
            return;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            logger.info("onOpen using subprotocol " + webSocket.getSubprotocol());
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            return Listener.super.onClose(webSocket, statusCode, reason);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            if (bTerminate) {
                logger.info(allNotificationMapS);
                logger.info("End Closure of listener ");
                onClose(webSocket, 0, "After suite invoked closing");
            }
            try {
                Root root = new Root();
                ObjectMapper om = new ObjectMapper();
                String message = "";
                String address = "";

                root = om.readValue(data.toString(), Root.class);
                if ("SMS".equalsIgnoreCase(root.type)) {
                    message = root.subject;
                    address = root.to.text.trim();
                } else if ("MAIL".equalsIgnoreCase(root.type)) {
                    message = root.html;
                    address = root.to.value.get(0).address;
                } else {
                    logger.warn("Unsupported notification type. type=" + root.type);
                    message = data.toString(); // store raw JSON if unknown type
                    address = "UNKNOWN";
                }

                // ✅ Always store the notification
                allNotificationMapS.put(address, message);
                logger.info("Stored notification for key = " + address + " data = " + data);

            } catch (Exception e) {
                logger.error("Error processing WebSocket message: " + e.getMessage(), e);
            }

            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            logger.info("Bad day! " + webSocket.toString());
            logger.error(error.getMessage(), error);
            WebSocket.Listener.super.onError(webSocket, error);
        }
    }

    public static String getNotification(String emailId) {
        int otpExpTime = AdminTestUtil.getOtpExpTimeFromActuator();
        int otpCheckLoopCount = (otpExpTime * 1000) / AdminTestUtil.OTP_CHECK_INTERVAL;

        int counter = 0;
        while (counter < otpCheckLoopCount) {
            logger.info("Checking notifications for emailId = " + emailId);
            if (allNotificationMapS.get(emailId) != null) {
                String message = (String) allNotificationMapS.get(emailId);
                allNotificationMapS.remove(emailId); 
                logger.info("Found notification for " + emailId + ": " + message);
                return message;  
            }
            counter++;
            try {
                Thread.sleep(AdminTestUtil.OTP_CHECK_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.info("Notification not found for " + emailId + " after retries");
        return "";
    }

    public static String parseAdditionalReqId(String message) {
        String additionalReqId = StringUtils.substringBetween(message, "AdditionalInfoRequestId",
                "-BIOMETRIC_CORRECTION-1");
        if (additionalReqId == null)
            return "";
        else
            return additionalReqId;
    }
}
