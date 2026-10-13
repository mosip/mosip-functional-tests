package io.mosip.testrig.uitestrig.accessibility;

/**
 * WCAG conformance levels, mapped to the axe-core tag strings accepted by {@code axe.run()}.
 *
 * Pass to {@link AccessibilityValidator#runAxeCore(com.microsoft.playwright.Page, WcagLevel)}
 * to control which WCAG rules are evaluated.
 *
 * <ul>
 *   <li>{@link #LEVEL_A}   — minimum conformance; the most critical issues</li>
 *   <li>{@link #LEVEL_AA}  — standard target for most web applications; includes Level A</li>
 *   <li>{@link #LEVEL_AAA} — highest conformance; includes Levels A and AA</li>
 * </ul>
 *
 * Use {@link #LEVEL_AA} by default — it corresponds to the WCAG 2.1 AA threshold
 * required by most government and public-sector accessibility mandates.
 */
public enum WcagLevel {

    LEVEL_A  ("wcag2a",  "wcag21a"),
    LEVEL_AA ("wcag2a",  "wcag21a", "wcag2aa", "wcag21aa"),
    LEVEL_AAA("wcag2a",  "wcag21a", "wcag2aa", "wcag21aa", "wcag2aaa");

    private final String[] axeTags;

    WcagLevel(String... axeTags) {
        this.axeTags = axeTags;
    }

    /**
     * Returns the axe-core tag array to pass as {@code runOnly.values} in the
     * {@code axe.run()} options object.
     */
    public String[] getAxeTags() {
        return axeTags;
    }
}
