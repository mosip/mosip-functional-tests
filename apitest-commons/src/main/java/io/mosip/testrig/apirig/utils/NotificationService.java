package io.mosip.testrig.apirig.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public final class NotificationService {

    private static final Logger logger = Logger.getLogger(NotificationService.class);

    private static final int MAX_QUEUE_SIZE = 50;
    private static final long INACTIVE_EXPIRY_MS = 15 * 60 * 1000; // 15 mins

    private static final ConcurrentHashMap<String, EmailQueue> otpQueues =
            new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, EmailQueue> notificationQueues =
            new ConcurrentHashMap<>();

    private static final ThreadLocal<Long> requestWatermark =
            ThreadLocal.withInitial(() -> 0L);

    private NotificationService() {}

    // --------------------------------------------------
    // Internal wrapper
    // --------------------------------------------------
    private static class EmailQueue {
        final BlockingQueue<OtpMessage> queue =
                new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
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

    private static void store(
            ConcurrentHashMap<String, EmailQueue> map,
            String email,
            String message) {

        EmailQueue emailQueue = map.computeIfAbsent(email, k -> new EmailQueue());
        emailQueue.lastAccessTime = System.currentTimeMillis();

        if (!emailQueue.queue.offer(new OtpMessage(message))) {
            emailQueue.queue.poll(); // remove oldest
            emailQueue.queue.offer(new OtpMessage(message));
            logger.warn("Queue full. Oldest message removed for " + email);
        }
    }

    public static String getOtp(String emailId) {
        return poll(otpQueues, emailId, true);
    }

    public static String getNotification(String emailId) {
        return poll(notificationQueues, emailId, false);
    }

    private static String poll(
            ConcurrentHashMap<String, EmailQueue> map,
            String emailId,
            boolean otpMode) {

        long afterTime = requestWatermark.get();
        if (afterTime == 0L) {
            logger.warn("markRequestStart() not called! Risk of stale message");
        }

        EmailQueue emailQueue =
                map.computeIfAbsent(emailId, k -> new EmailQueue());

        long timeoutSeconds = AdminTestUtil.getOtpExpTimeFromActuator();
        long endTime = System.currentTimeMillis() + timeoutSeconds * 1000;

        try {
            while (System.currentTimeMillis() < endTime) {

                OtpMessage msg = emailQueue.queue.poll(5, TimeUnit.SECONDS);
                if (msg == null) continue;

                if (msg.receivedAt < afterTime) {
                    logger.info("Skipping stale message for " + emailId);
                    continue;
                }

                emailQueue.lastAccessTime = System.currentTimeMillis();

                if (!otpMode) {
                    logger.info("[Thread:" + Thread.currentThread().getName()
                            + "] Found notification for " + emailId);
                    return msg.message;
                }

                String otp = parseOtp(msg.message);
                if (!otp.isEmpty()) {
                    logger.info("[Thread:" + Thread.currentThread().getName()
                            + "] Found OTP for " + emailId + " : " + otp);
                    return otp;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return "";
    }

    public static String parseOtp(String message) {
        if (message == null) return "";
        Pattern pattern = Pattern.compile("(|^)\\s\\d{6}\\s");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(0).trim() : "";
    }

    // --------------------------------------------------
    // Cleanup Thread
    // --------------------------------------------------

    static {
        startCleanupTask();
    }

    private static void startCleanupTask() {

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                });

        scheduler.scheduleAtFixedRate(() -> {

            long now = System.currentTimeMillis();

            otpQueues.entrySet().removeIf(e ->
                    now - e.getValue().lastAccessTime > INACTIVE_EXPIRY_MS);

            notificationQueues.entrySet().removeIf(e ->
                    now - e.getValue().lastAccessTime > INACTIVE_EXPIRY_MS);

            logger.info("Cleanup executed. Active email queues: "
                    + otpQueues.size());

        }, 5, 5, TimeUnit.MINUTES);
    }
}