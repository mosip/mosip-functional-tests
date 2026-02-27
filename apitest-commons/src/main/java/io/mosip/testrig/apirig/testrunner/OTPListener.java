package io.mosip.testrig.apirig.testrunner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.testrig.apirig.otp.Root;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.NotificationListener;

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

			URI iamUri = URI.create(ConfigManager.getIAMUrl());

			String host = iamUri.getHost();
			if (host == null) {
				throw new IllegalStateException("Invalid IAM URL: " + ConfigManager.getIAMUrl());
			}

			int firstDot = host.indexOf('.');

			if (firstDot == -1 || firstDot == host.length() - 1) {
				throw new IllegalStateException("Unexpected IAM host: " + host);
			}

			String domain = host.substring(firstDot + 1);

			String smtpHost = "smtp." + domain;

			String websocketUrl = "wss://" + smtpHost + "/mocksmtp/websocket";

			logger.info("Connecting OTP WebSocket: " + websocketUrl);

			HTTP_CLIENT.newWebSocketBuilder().buildAsync(URI.create(websocketUrl), new WebSocketClient()).get(30,
					TimeUnit.SECONDS);

		} catch (Exception e) {
			logger.error("Failed to start OTP WebSocket listener", e);
		}
	}

	// --------------------------------------------------
	// WebSocket Client
	// --------------------------------------------------
	private static class WebSocketClient implements WebSocket.Listener {

		private final ObjectMapper objectMapper = new ObjectMapper();
		private final StringBuilder messageBuffer = new StringBuilder();

		@Override
		public void onOpen(WebSocket webSocket) {
			logger.info("OTP WebSocket connection opened.");
			Listener.super.onOpen(webSocket);
		}

		@Override
		public synchronized CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

			if (bTerminate) {
				logger.info("OTP Listener terminating...");
				webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Test Suite Completed");
				return Listener.super.onText(webSocket, data, last);
			}

			// ✅ accumulate fragments
			messageBuffer.append(data);

			// ✅ wait until full message received
			if (!last) {
				webSocket.request(1);
				return Listener.super.onText(webSocket, data, false);
			}

			String completeMessage = messageBuffer.toString();
			messageBuffer.setLength(0);

			try {

				Root root = objectMapper.readValue(completeMessage, Root.class);

				String otpMessage = "";
				String notificationMessage = "";
				String address = "";

				if ("SMS".equalsIgnoreCase(root.type)) {

					otpMessage = root.subject;
					notificationMessage = root.subject;

					if (root.to == null || root.to.text == null) {
						logger.warn("SMS notification missing recipient");
						return Listener.super.onText(webSocket, data, true);
					}

					address = root.to.text.trim();

				} else if ("MAIL".equalsIgnoreCase(root.type)) {

					otpMessage = root.html;
					notificationMessage = root.subject;

					if (root.to == null || root.to.value == null || root.to.value.isEmpty()
							|| root.to.value.get(0).address == null) {

						logger.warn("MAIL notification missing recipient");
						return Listener.super.onText(webSocket, data, true);
					}

					address = root.to.value.get(0).address.trim();

				} else {
					logger.warn("Unsupported notification type: " + root.type);
					return Listener.super.onText(webSocket, data, true);
				}

				logger.info(String.format("[Thread:%s] Notification received for %s", Thread.currentThread().getName(),
						address));

				// 🔔 Store ALL notifications
				NotificationListener.storeNotification(address, notificationMessage);

				// 🔐 Store OTP if present
				if (!NotificationListener.parseOtp(otpMessage).isEmpty()) {
					NotificationListener.storeOtp(address, otpMessage);
				}

				// 🔐 Store Additional Request Id if present
				if (!NotificationListener.parseAdditionalReqId(otpMessage).isEmpty()) {
					NotificationListener.storeWorkflowMessage(address, otpMessage);
				}

			} catch (Exception e) {
				logger.error("Error processing WebSocket message", e);
			}

			webSocket.request(1);
			return Listener.super.onText(webSocket, data, true);
		}

		@Override
		public void onError(WebSocket webSocket, Throwable error) {
			logger.error("WebSocket error occurred", error);
			Listener.super.onError(webSocket, error);
		}

		@Override
		public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
			logger.warn("WebSocket closed. Status: " + statusCode + " Reason: " + reason);
			return Listener.super.onClose(webSocket, statusCode, reason);
		}
	}
}