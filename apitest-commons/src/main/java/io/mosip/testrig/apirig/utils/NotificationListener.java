package io.mosip.testrig.apirig.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public final class NotificationListener {

	private static final Logger logger = Logger.getLogger(NotificationListener.class);

	private static final int MAX_QUEUE_SIZE = 50;
	private static final long INACTIVE_EXPIRY_MS = 15 * 60 * 1000; // 15 mins
	private static final Pattern OTP_PATTERN = Pattern.compile("\\b(\\d{6})\\b");
	private static final Pattern ADDITIONAL_REQ_PATTERN = Pattern
			.compile("AdditionalInfoRequestId\\s*[:=]?\\s*([A-Za-z0-9-]+)-BIOMETRIC_CORRECTION-1");
	private static final ConcurrentHashMap<String, EmailQueue> otpQueues = new ConcurrentHashMap<>();

	private static final ConcurrentHashMap<String, EmailQueue> notificationQueues = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, EmailQueue> workflowQueues = new ConcurrentHashMap<>();

	private static final ThreadLocal<Long> requestWatermark = ThreadLocal.withInitial(() -> 0L);

	private NotificationListener() {
	}

	// --------------------------------------------------
	// Internal wrapper
	// --------------------------------------------------
	private static class EmailQueue {
		final BlockingQueue<OtpMessage> queue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
		volatile long lastAccessTime = System.currentTimeMillis();
	}

	private static class OtpMessage {
		final String message;
		final long receivedAt = System.currentTimeMillis();

		OtpMessage(String message) {
			this.message = message;
		}
	}

	// --------------------------------------------------
	// Public API
	// --------------------------------------------------

	public static void markRequestStart() {
		requestWatermark.set(System.currentTimeMillis());
	}

	public static void markRequestRemove() {
		requestWatermark.remove();
	}

	public static void storeNotification(String email, String message) {
		store(notificationQueues, email, message);
	}

	public static void storeOtp(String email, String message) {
		store(otpQueues, email, message);
	}

	public static void storeWorkflowMessage(String email, String message) {
		store(workflowQueues, email, message);
	}

	private static void store(ConcurrentHashMap<String, EmailQueue> map, String email, String message) {

		EmailQueue emailQueue = map.computeIfAbsent(email, k -> new EmailQueue());
		emailQueue.lastAccessTime = System.currentTimeMillis();

		OtpMessage newMessage = new OtpMessage(message);
		while (!emailQueue.queue.offer(newMessage)) {
			emailQueue.queue.poll(); // remove oldest and retry insert
			logger.warn("Queue full. Oldest message removed for " + email);
		}
	}

	public static String getOtp(String emailId) {
		if (ConfigManager.getUsePreConfiguredOtp().equalsIgnoreCase(GlobalConstants.TRUE_STRING)) {

			logger.info("Using PreConfigured OTP");
			return ConfigManager.getPreConfiguredOtp();
		}

		return parseOtp(poll(otpQueues, emailId, m -> !parseOtp(m).isEmpty()));
	}

	public static String getNotification(String emailId) {
		return poll(notificationQueues, emailId, m -> true);
	}

	public static String getAdditionalReqId(String emailId) {

		String msg = poll(workflowQueues, emailId, m -> !parseAdditionalReqId(m).isEmpty());

		return parseAdditionalReqId(msg);
	}

	public static String getNotification(String emailId, String expectedText) {

		return poll(notificationQueues, emailId,
				m -> m != null && m.toLowerCase().contains(expectedText.toLowerCase()));
	}

	private static String poll(ConcurrentHashMap<String, EmailQueue> map, String emailId, Predicate<String> filter) {

		long afterTime = requestWatermark.get();

		EmailQueue emailQueue = map.computeIfAbsent(emailId, k -> new EmailQueue());

		long timeoutSeconds = AdminTestUtil.getOtpExpTimeFromActuator();

		long endTime = System.currentTimeMillis() + timeoutSeconds * 1000;

		try {

			while (System.currentTimeMillis() < endTime) {

				OtpMessage msg = emailQueue.queue.poll(5, TimeUnit.SECONDS);

				if (msg == null)
					continue;

				if (msg.receivedAt < afterTime) {
					logger.info("Skipping stale message for " + emailId);
					continue;
				}

				emailQueue.lastAccessTime = System.currentTimeMillis();

				if (filter.test(msg.message)) {
					return msg.message;
				}
			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		return "";
	}

	public static String parseOtp(String message) {
		if (message == null)
			return "";
		Matcher matcher = OTP_PATTERN.matcher(message);
		return matcher.find() ? matcher.group(1) : "";
	}

	public static String parseAdditionalReqId(String message) {

		if (message == null)
			return "";

		int index = message.indexOf("AdditionalInfoRequestId");
		if (index < 0) {
			return "";
		}

		String sub = message.substring(index);

		Matcher matcher = ADDITIONAL_REQ_PATTERN.matcher(sub);

		return matcher.find() ? matcher.group(1).trim() : "";
	}

	// --------------------------------------------------
	// Cleanup Thread
	// --------------------------------------------------

	static {
		startCleanupTask();
	}

	private static void startCleanupTask() {

		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		});

		scheduler.scheduleAtFixedRate(() -> {

			long now = System.currentTimeMillis();

			otpQueues.entrySet().removeIf(e -> now - e.getValue().lastAccessTime > INACTIVE_EXPIRY_MS);

			notificationQueues.entrySet().removeIf(e -> now - e.getValue().lastAccessTime > INACTIVE_EXPIRY_MS);

			workflowQueues.entrySet().removeIf(e -> now - e.getValue().lastAccessTime > INACTIVE_EXPIRY_MS);

			logger.info("Cleanup executed. Active email queues - otp: " + otpQueues.size() + ", notification: "
					+ notificationQueues.size() + ", workflow: " + workflowQueues.size());

		}, 5, 5, TimeUnit.MINUTES);
	}
}