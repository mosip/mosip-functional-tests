package io.mosip.testrig.uitestrig.assertions;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Soft assertion wrapper — collects all assertion failures and throws them together.
 *
 * Each {@code verify*()} method catches any {@link AssertionError} or
 * {@link RuntimeException} thrown by the underlying {@link AssertionLibrary} and
 * appends it to an internal list. {@link #assertAll()} throws a single
 * {@link AssertionError} that lists every failure, making it easy to see the
 * full picture of a scenario's failures in one shot rather than stopping at the first.
 *
 * <pre>
 *   SoftAssertions soft = new SoftAssertions(page);
 *   soft.verifyText("#header", "Manage My VID");
 *   soft.verifyVisible("#addVidBtn");
 *   soft.verifyURLContains("/managemyvid");
 *   soft.assertAll();   // throws with all failures listed if any failed
 * </pre>
 *
 * The instance is reusable: call {@link #reset()} to clear failures between assertion groups.
 */
public class SoftAssertions {

    private static final Logger logger = LoggerFactory.getLogger(SoftAssertions.class);

    private final AssertionLibrary assertions;
    private final List<String> failures = new ArrayList<>();

    public SoftAssertions(Page page) {
        this.assertions = new AssertionLibrary(page);
    }

    // ── Delegation helpers ────────────────────────────────────────────────────

    private void soft(String label, Runnable assertion) {
        try {
            assertion.run();
        } catch (AssertionError | RuntimeException e) {
            String msg = "[FAIL] " + label + "\n       " + e.getMessage();
            failures.add(msg);
            logger.warn("Soft assertion failed: {}", label);
        }
    }

    // ── Visibility / State ────────────────────────────────────────────────────

    public SoftAssertions verifyVisible(String selector) {
        soft("verifyVisible(\"" + selector + "\")",
             () -> assertions.verifyVisible(selector));
        return this;
    }

    public SoftAssertions verifyNotVisible(String selector) {
        soft("verifyNotVisible(\"" + selector + "\")",
             () -> assertions.verifyNotVisible(selector));
        return this;
    }

    public SoftAssertions verifyEnabled(String selector) {
        soft("verifyEnabled(\"" + selector + "\")",
             () -> assertions.verifyEnabled(selector));
        return this;
    }

    public SoftAssertions verifyDisabled(String selector) {
        soft("verifyDisabled(\"" + selector + "\")",
             () -> assertions.verifyDisabled(selector));
        return this;
    }

    // ── Text ──────────────────────────────────────────────────────────────────

    public SoftAssertions verifyText(String selector, String expected) {
        soft("verifyText(\"" + selector + "\", \"" + expected + "\")",
             () -> assertions.verifyText(selector, expected));
        return this;
    }

    public SoftAssertions verifyPartialText(String selector, String contains) {
        soft("verifyPartialText(\"" + selector + "\", \"" + contains + "\")",
             () -> assertions.verifyPartialText(selector, contains));
        return this;
    }

    public SoftAssertions verifyInputValue(String selector, String expected) {
        soft("verifyInputValue(\"" + selector + "\", \"" + expected + "\")",
             () -> assertions.verifyInputValue(selector, expected));
        return this;
    }

    // ── Count ─────────────────────────────────────────────────────────────────

    public SoftAssertions verifyCount(String selector, int expected) {
        soft("verifyCount(\"" + selector + "\", " + expected + ")",
             () -> assertions.verifyCount(selector, expected));
        return this;
    }

    // ── URL & Title ───────────────────────────────────────────────────────────

    public SoftAssertions verifyURL(String pattern) {
        soft("verifyURL(\"" + pattern + "\")",
             () -> assertions.verifyURL(pattern));
        return this;
    }

    public SoftAssertions verifyURLContains(String fragment) {
        soft("verifyURLContains(\"" + fragment + "\")",
             () -> assertions.verifyURLContains(fragment));
        return this;
    }

    public SoftAssertions verifyTitle(String expected) {
        soft("verifyTitle(\"" + expected + "\")",
             () -> assertions.verifyTitle(expected));
        return this;
    }

    public SoftAssertions verifyTitleContains(String partial) {
        soft("verifyTitleContains(\"" + partial + "\")",
             () -> assertions.verifyTitleContains(partial));
        return this;
    }

    // ── Attributes ────────────────────────────────────────────────────────────

    public SoftAssertions verifyAttributeValue(String selector, String attr, String expected) {
        soft("verifyAttributeValue(\"" + selector + "\", \"" + attr + "\", \"" + expected + "\")",
             () -> assertions.verifyAttributeValue(selector, attr, expected));
        return this;
    }

    public SoftAssertions verifyAttributeContains(String selector, String attr, String fragment) {
        soft("verifyAttributeContains(\"" + selector + "\", \"" + attr + "\", \"" + fragment + "\")",
             () -> assertions.verifyAttributeContains(selector, attr, fragment));
        return this;
    }

    // ── CSS Color ─────────────────────────────────────────────────────────────

    public SoftAssertions verifyColor(String selector, String cssProperty, String expectedHex) {
        soft("verifyColor(\"" + selector + "\", \"" + cssProperty + "\", \"" + expectedHex + "\")",
             () -> assertions.verifyColor(selector, cssProperty, expectedHex));
        return this;
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    public SoftAssertions verifyTable(String tableSelector, List<List<String>> expectedData) {
        soft("verifyTable(\"" + tableSelector + "\")",
             () -> assertions.verifyTable(tableSelector, expectedData));
        return this;
    }

    // ── Download ──────────────────────────────────────────────────────────────

    public SoftAssertions verifyDownload(String expectedFileNamePattern, Runnable triggerAction) {
        soft("verifyDownload(\"" + expectedFileNamePattern + "\")",
             () -> assertions.verifyDownload(expectedFileNamePattern, triggerAction));
        return this;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Throws an {@link AssertionError} listing all collected failures.
     * Does nothing if no failures were recorded.
     */
    public void assertAll() {
        if (failures.isEmpty()) return;
        String combined = "Soft assertion failures (" + failures.size() + "):\n\n"
                + String.join("\n\n", failures);
        throw new AssertionError(combined);
    }

    /** Returns true if at least one assertion has failed. */
    public boolean hasFailed() {
        return !failures.isEmpty();
    }

    /** Returns an unmodifiable view of the accumulated failure messages. */
    public List<String> getFailures() {
        return Collections.unmodifiableList(failures);
    }

    /** Clears accumulated failures. Use to reuse this instance across assertion groups. */
    public void reset() {
        failures.clear();
    }
}
