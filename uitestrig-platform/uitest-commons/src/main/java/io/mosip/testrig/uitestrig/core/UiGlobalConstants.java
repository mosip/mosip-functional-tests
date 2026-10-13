package io.mosip.testrig.uitestrig.core;

/**
 * Framework-level constants shared across all MOSIP UI test modules.
 * Module-specific selectors belong in each module's own GlobalConstants class.
 */
public class UiGlobalConstants {

    // ── Generic portal element IDs used by BasePage utilities ─────────────────
    public static final String DISMISS_BTN  = "#dismissBtn";
    public static final String MESSAGE_POPUP = "#messagePopup";

    // ── Report status tokens ───────────────────────────────────────────────────
    public static final String REPORT_PASS = "PASS";
    public static final String REPORT_FAIL = "FAIL";
    public static final String REPORT_SKIP = "SKIP";

    // ── Exception message prefix ───────────────────────────────────────────────
    public static final String EXCEPTION_PREFIX = "Exception: ";

    // ── Snapshot diff tolerance (percentage of pixels that may differ) ─────────
    public static final double SNAPSHOT_MAX_DIFF_RATIO = 0.02;

    private UiGlobalConstants() {}
}
