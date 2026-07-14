package io.mosip.testrig.apirig.testrunner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.testrig.apirig.otp.Root;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.NotificationListener;

public class OTPListener {

	private static final Logger logger = Logger.getLogger(OTPListener.class);

	private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

	public static volatile boolean bTerminate = false;

	private static final AtomicBoolean reconnecting = new AtomicBoolean(false);

	public static final ConcurrentLinkedQueue<String> reconnectEvents = new ConcurrentLinkedQueue<>();

	public OTPListener() {
		if (ConfigManager.IsDebugEnabled()) {
			logger.setLevel(Level.ALL);
		} else {
			logger.setLevel(Level.ERROR);
		}
	}

	private static final String WEBSOCKET_PATH = "/mocksmtp/websocket";

	// --------------------------------------------------
	// Resolve WebSocket URL
	// --------------------------------------------------
	private static String resolveWebsocketUrl() {
		String smtpUrl = ConfigManager.getSmtpUrl();
		String smtpHost = (smtpUrl != null && !smtpUrl.isBlank()) ? extractHost(smtpUrl) : deriveSmtpHostFromIam();
		return "wss://" + smtpHost + WEBSOCKET_PATH;
	}

	private static String extractHost(String url) {
		String trimmed = url.trim();
		URI uri = URI.create(trimmed.contains("://") ? trimmed : "https://" + trimmed);
		String host = uri.getHost();
		if (host == null) {
			throw new IllegalStateException("Invalid smtpURL: " + url);
		}
		return host;
	}

	private static String deriveSmtpHostFromIam() {
		String iamUrl = ConfigManager.getIAMUrl();
		String host = URI.create(iamUrl).getHost();
		if (host == null) {
			throw new IllegalStateException("Invalid IAM URL: " + iamUrl);
		}

		int firstDot = host.indexOf('.');
		if (firstDot == -1 || firstDot == host.length() - 1) {
			throw new IllegalStateException("Unexpected IAM host: " + host);
		}

		return "smtp." + host.substring(firstDot + 1);
	}

	// --------------------------------------------------
	// Start WebSocket Listener
	// --------------------------------------------------
	public void run() {
		try {

			String websocketUrl = resolveWebsocketUrl();

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
		private ScheduledExecutorService pingScheduler;

		@Override
		public void onOpen(WebSocket webSocket) {
			logger.info("OTP WebSocket connection opened.");
			startPing(webSocket);
			Listener.super.onOpen(webSocket);
		}

		private void startPing(WebSocket webSocket) {
			pingScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
				Thread t = new Thread(r, "ws-ping");
				t.setDaemon(true);
				return t;
			});
			pingScheduler.scheduleAtFixedRate(() -> {
				if (bTerminate) {
					pingScheduler.shutdown();
					return;
				}
				try {
					webSocket.sendPing(ByteBuffer.wrap("ping".getBytes()));
				} catch (Exception e) {
					logger.warn("WebSocket ping failed: " + e.getMessage());
				}
			}, 30, 30, TimeUnit.SECONDS);
		}

		private void stopPing() {
			if (pingScheduler != null && !pingScheduler.isShutdown()) {
				pingScheduler.shutdownNow();
			}
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

				logger.info("RAW WEBSOCKET MESSAGE RECEIVED length=" + completeMessage.length());
				logger.debug("RAW MESSAGE >>> " + completeMessage);
				JsonNode jsonNode = objectMapper.readTree(completeMessage);
				Root root = objectMapper.treeToValue(jsonNode, Root.class);

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
			stopPing();
			scheduleReconnect("error");
			Listener.super.onError(webSocket, error);
		}

		@Override
		public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
			logger.warn("WebSocket closed. Status: " + statusCode + " Reason: " + reason);
			stopPing();
			if (statusCode != 1000) {
				scheduleReconnect("abnormal close (status=" + statusCode + ")");
			}
			return Listener.super.onClose(webSocket, statusCode, reason);
		}

		private void scheduleReconnect(String trigger) {
			if (bTerminate) return;
			if (!reconnecting.compareAndSet(false, true)) {
				logger.info("Reconnect already in progress, ignoring trigger: " + trigger);
				return;
			}
			logger.info("Scheduling WebSocket reconnect after " + trigger);
			Thread t = new Thread(() -> {
				try {
					int attempt = 0;
					while (!bTerminate) {
						attempt++;
						long delayMs = Math.min(3000L * attempt, 60000L);
						try {
							Thread.sleep(delayMs);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							return;
						}
						if (bTerminate) return;
						try {
							String websocketUrl = resolveWebsocketUrl();
							logger.info("Reconnecting WebSocket (attempt " + attempt + "): " + websocketUrl);
							HTTP_CLIENT.newWebSocketBuilder()
									.buildAsync(URI.create(websocketUrl), new WebSocketClient())
									.get(30, TimeUnit.SECONDS);
							logger.info("WebSocket reconnected successfully on attempt " + attempt);
							reconnectEvents.add("WebSocket reconnected successfully on attempt "
									+ attempt + " at " + java.time.Instant.now() + " UTC");
							return;
						} catch (Exception e) {
							String failMsg = "WebSocket reconnect attempt " + attempt + " failed: "
									+ e.getMessage() + " at " + java.time.Instant.now() + " UTC";
							logger.warn(failMsg + ". Retrying in "
									+ Math.min(3000L * (attempt + 1), 60000L) + "ms...");
							reconnectEvents.add(failMsg);
						}
					}
				} finally {
					reconnecting.set(false);
				}
			}, "ws-reconnect");
			t.setDaemon(true);
			t.start();
		}
	}
}