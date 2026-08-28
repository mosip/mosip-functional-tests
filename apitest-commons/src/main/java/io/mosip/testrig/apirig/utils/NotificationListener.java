package io.mosip.testrig.apirig.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public final class NotificationListener {

	private static final Logger logger = Logger.getLogger(NotificationListener.class);

	private static final int MAX_QUEUE_SIZE = 50;
	private static final long INACTIVE_EXPIRY_MS = Long.parseLong(ConfigManager.getproperty("otp_queue_inactive_expiry_time")) * 60 * 1000; // 15 mins

	// How long to keep watching the same identity's queue for a newer message before committing to "latest" - a fresh send-otp for a shared identity can supersede ours moments later, see poll().
	private static final long LATEST_MESSAGE_QUIET_GAP_MS = parseNonNegativeLongOrDefault(
			ConfigManager.getproperty("otp_latest_pick_quiet_gap_ms"), 200L);
	// Hard cap on total time spent watching for a newer message, in case the queue keeps receiving messages back-to-back.
	private static final long LATEST_MESSAGE_MAX_SETTLE_MS = parseNonNegativeLongOrDefault(
			ConfigManager.getproperty("otp_latest_pick_max_settle_ms"), 800L);

	// Keyword-anchored branches only, used to CLASSIFY whether a message is OTP-bearing at all (see isLikelyOtpMessage()) without matching an unrelated six-digit number (encryption key, request id, ...).
	private static final Pattern OTP_ANCHORED_PATTERN = Pattern.compile(
			"(?i)(?:" +
			"\\bis\\s+(\\d{6})\\s+and\\s+is\\s+valid" +   // English: "is XXXXXX and is valid"
			"|\\buse\\s+(?:otp\\s+)?(\\d{6})\\b" +        // English: "Use [OTP] XXXXXX"
			"|\\bOTP\\b.*?\\b(\\d{6})\\b" +               // Multilingual: OTP keyword + standalone 6 digits
			")");
	// Same as OTP_ANCHORED_PATTERN plus a last-resort unanchored branch, for extracting digits once a message is already trusted to be OTP-bearing - not used for classification, see above.
	private static final Pattern OTP_PATTERN = Pattern.compile(
			"(?i)(?:" +
			"\\bis\\s+(\\d{6})\\s+and\\s+is\\s+valid" +
			"|\\buse\\s+(?:otp\\s+)?(\\d{6})\\b" +
			"|\\bOTP\\b.*?\\b(\\d{6})\\b" +
			"|\\b(\\d{6})\\b" +
			")");
	private static final ConcurrentHashMap<String, EmailQueue> otpQueues = new ConcurrentHashMap<>();

	private static final ConcurrentHashMap<String, EmailQueue> notificationQueues = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, EmailQueue> workflowQueues = new ConcurrentHashMap<>();

	private static final ThreadLocal<Long> requestWatermark = ThreadLocal.withInitial(() -> 0L);

	private NotificationListener() {
	}

	private static long parseNonNegativeLongOrDefault(String raw, long defaultValue) {
		if (raw == null || raw.trim().isEmpty()) {
			return defaultValue;
		}
		try {
			long value = Long.parseLong(raw.trim());
			return value >= 0 ? value : defaultValue;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	// --------------------------------------------------
	// Internal wrapper
	// --------------------------------------------------
	private static class EmailQueue {
		final BlockingQueue<OtpMessage> queue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
		// Serializes poll()'s "find a match, then watch for a newer one" sequence per identity - scoped per-queue so different identities never contend, see poll().
		final ReentrantLock pollLock = new ReentrantLock();
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

		logger.info(String.format("[STORE] Email=%s QueueSize(before)=%d MessageSnippet=%s", email,
				emailQueue.queue.size(), message.length() > 80 ? message.substring(0, 80) + "..." : message));

		while (!emailQueue.queue.offer(newMessage)) {
			emailQueue.queue.poll(); // remove oldest and retry insert
			logger.warn("Queue full. Oldest message removed for " + email);
		}

		logger.info(String.format("[STORE] Email=%s QueueSize(after)=%d", email, emailQueue.queue.size()));
	}

	public static String getOtp(String emailId) {

		logger.info("Requesting OTP for email: " + emailId);

		if (ConfigManager.getUsePreConfiguredOtp().equalsIgnoreCase(GlobalConstants.TRUE_STRING)) {

			logger.info("Using PreConfigured OTP for email: " + emailId);
			return ConfigManager.getPreConfiguredOtp();
		}

		String msg = poll(otpQueues, emailId, m -> !parseOtp(m).isEmpty());

		String otp = parseOtp(msg);

		if (!otp.isEmpty()) {
			logger.info("OTP FOUND for email=" + emailId + " OTP=" + otp);
		} else {
			logger.warn("OTP NOT FOUND for email=" + emailId);
		}

		return otp;
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

		logger.info(String.format("[POLL START] Email=%s Timeout=%s seconds QueueSize=%d", emailId, timeoutSeconds,
				emailQueue.queue.size()));

		// Only serializes callers polling the SAME identity - see EmailQueue.pollLock.
		emailQueue.pollLock.lock();
		try {

			int loop = 0;

			try {

				while (System.currentTimeMillis() < endTime) {

					loop++;

					OtpMessage msg = emailQueue.queue.poll(5, TimeUnit.SECONDS);

					if (msg == null) {

						logger.info(String.format("[POLL LOOP %d] No message yet for email=%s", loop, emailId));

						continue;
					}

					logger.info(String.format("[POLL LOOP %d] Message received for email=%s ReceivedAt=%d", loop,
							emailId, msg.receivedAt));

					if (msg.receivedAt < afterTime) {
						logger.info(String.format("[POLL LOOP %d] Skipping stale message for email=%s", loop, emailId));
						continue;
					}

					emailQueue.lastAccessTime = System.currentTimeMillis();

					if (!filter.test(msg.message)) {
						logger.info(String.format("[POLL LOOP %d] Message did not match filter for email=%s", loop,
								emailId));
						continue;
					}

					// Matched, but don't return it yet - a newer request for the same identity can supersede it moments later, so watch briefly and keep the newest match seen.
					OtpMessage latest = msg;
					long settleDeadline = Math.min(endTime, System.currentTimeMillis() + LATEST_MESSAGE_MAX_SETTLE_MS);

					while (true) {

						long waitMs = Math.min(LATEST_MESSAGE_QUIET_GAP_MS, settleDeadline - System.currentTimeMillis());
						if (waitMs <= 0) {
							break;
						}

						OtpMessage next = emailQueue.queue.poll(waitMs, TimeUnit.MILLISECONDS);
						if (next == null) {
							// Quiet for LATEST_MESSAGE_QUIET_GAP_MS with nothing new - settle complete.
							break;
						}

						if (next.receivedAt < afterTime) {
							logger.info(String.format("[POLL SETTLE] Skipping stale message for email=%s", emailId));
							continue;
						}

						if (!filter.test(next.message)) {
							logger.info(String.format("[POLL SETTLE] Non-matching message for email=%s, ignored",
									emailId));
							continue;
						}

						logger.info(String.format(
								"[POLL SETTLE] Newer message for email=%s (ReceivedAt=%d) supersedes previous candidate (ReceivedAt=%d)",
								emailId, next.receivedAt, latest.receivedAt));

						emailQueue.lastAccessTime = System.currentTimeMillis();
						latest = next;
					}

					logger.info(String.format("[POLL SUCCESS] Email=%s Loop=%d MessageMatched", emailId, loop));
					return latest.message;
				}

			} catch (InterruptedException e) {
				logger.error("Polling interrupted for email=" + emailId, e);
				Thread.currentThread().interrupt();
			}

			logger.warn("[POLL TIMEOUT] No message received for email=" + emailId);

			return "";

		} finally {
			emailQueue.pollLock.unlock();
		}
	}

	// Whether a message actually looks like an OTP notification, not just any message with a coincidental six-digit number - use this (not parseOtp().isEmpty()) to decide whether to queue a message as OTP.
	public static boolean isLikelyOtpMessage(String message) {
		return message != null && OTP_ANCHORED_PATTERN.matcher(message).find();
	}

	public static String parseOtp(String message) {
		if (message == null) {
			logger.info("parseOtp called with null message");
			return "";
		}
		Matcher matcher = OTP_PATTERN.matcher(message);
		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				if (matcher.group(i) != null) {
					logger.info("Extracted OTP=" + matcher.group(i) + " from message");
					return matcher.group(i);
				}
			}
		}

		logger.info("OTP not found in message");

		return "";
	}

	public static String parseAdditionalReqId(String message) {

		if (message == null || message.isEmpty()) {
			logger.info("parseAdditionalReqId called with empty message");
			return "";
		}

		final String prefix = "AdditionalInfoRequestId";
		final String suffix = "-BIOMETRIC_CORRECTION-1";

		int start = message.indexOf(prefix);

		if (start < 0) {
			logger.info("AdditionalReqId prefix not found");
			return "";
		}

		start += prefix.length();

		int end = message.indexOf(suffix, start);

		if (end < 0) {
			logger.info("AdditionalReqId suffix not found");
			return "";
		}

		String value = message.substring(start, end);

		value = value.replace(":", "").replace("=", "").trim();

		logger.info("Extracted AdditionalReqId=" + value);

		return value;
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
