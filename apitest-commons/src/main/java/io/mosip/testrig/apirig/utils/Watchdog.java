package io.mosip.testrig.apirig.utils;

public class Watchdog {
	private final long timeoutMillis;
    private Thread watchdogThread;
    private boolean running = false;

    public Watchdog(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public void start() {
        if (running) return;
        running = true;
        watchdogThread = new Thread(() -> {
            try {
                Thread.sleep(timeoutMillis);
                System.err.println("Watchdog timeout exceeded. Terminating the application.");
                System.exit(1); // Forcefully terminate after timeout
            } catch (InterruptedException e) {
                System.out.println("Watchdog interrupted before timeout.");
            }
        });
        watchdogThread.setDaemon(true); // Allows JVM to exit if main ends early
        watchdogThread.start();
    }

    public void stop() {
        if (watchdogThread != null && watchdogThread.isAlive()) {
            watchdogThread.interrupt(); // Cancel the watchdog
            running = false;
        }
    }


}
