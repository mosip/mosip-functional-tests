package io.mosip.testrig.uitestrig.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.mosip.testrig.uitestrig.accessibility.AccessibilityValidator;
import io.mosip.testrig.uitestrig.accessibility.WcagLevel;
import io.mosip.testrig.uitestrig.assertions.AssertionLibrary;
import io.mosip.testrig.uitestrig.assertions.SoftAssertions;
import io.mosip.testrig.uitestrig.browser.SelfHealingLocator;
import io.mosip.testrig.uitestrig.browser.SmartWait;
import io.mosip.testrig.uitestrig.elements.Button;
import io.mosip.testrig.uitestrig.elements.DatePicker;
import io.mosip.testrig.uitestrig.elements.Dialog;
import io.mosip.testrig.uitestrig.elements.DropDown;
import io.mosip.testrig.uitestrig.elements.Table;
import io.mosip.testrig.uitestrig.elements.TextBox;
import io.mosip.testrig.uitestrig.elements.Toast;
import io.mosip.testrig.uitestrig.reporting.UiExtentReportManager;
import io.mosip.testrig.uitestrig.visual.VisualDiffEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Base64;

/**
 * Playwright interaction helpers for all page objects.
 * No Thread.sleep — Playwright's auto-wait + SmartWait handle synchronization.
 */
public abstract class BasePage {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    // ── Navigation & waits ────────────────────────────────────────────────────

    public void navigateTo(String url) {
        UiExtentReportManager.logStep("Navigate to: " + url);
        try {
            page.navigate(url);
            waitForNetworkIdle();
        } catch (RuntimeException e) {
            captureFailure("Navigate to: " + url, url, e);
            throw e;
        }
    }

    /** Waits until the network is idle. Delegates to SmartWait. */
    public void waitForNetworkIdle() {
        SmartWait.forNetworkIdle(page);
    }

    /** Waits until Angular's NgZone is stable. Use on Angular portals after actions. */
    public void waitForAngular() {
        SmartWait.forAngular(page);
    }

    public void waitForVisible(String selector) {
        waitForVisible(selector, "Wait for " + humanize(selector) + " to be visible");
    }

    /** As {@link #waitForVisible(String)} but with a caller-supplied report description. */
    public void waitForVisible(String selector, String description) {
        UiExtentReportManager.logStepWithLocator(description, selector);
        try {
            SmartWait.forVisible(page.locator(selector));
        } catch (RuntimeException e) {
            captureFailure(description, selector, e);
            throw e;
        }
    }

    // ── Basic interactions ────────────────────────────────────────────────────

    public void click(String selector) {
        click(selector, "Click " + humanize(selector));
    }

    /** As {@link #click(String)} but with a caller-supplied report description. */
    public void click(String selector, String description) {
        UiExtentReportManager.logStepWithLocator(description, selector);
        try {
            page.locator(selector).click();
        } catch (RuntimeException e) {
            captureFailure(description, selector, e);
            throw e;
        }
    }

    /**
     * As {@link #click(String, String)} but clicks the {@code nth} match (0-based) of a
     * selector that resolves to several elements — e.g. a row action in a list.
     */
    public void click(String selector, String description, int nth) {
        String locator = selector + " » nth(" + nth + ")";
        UiExtentReportManager.logStepWithLocator(description, locator);
        try {
            page.locator(selector).nth(nth).click();
        } catch (RuntimeException e) {
            captureFailure(description, locator, e);
            throw e;
        }
    }

    public void clickByText(String text) {
        UiExtentReportManager.logStep("Click '" + text + "'");
        try {
            page.getByText(text).click();
        } catch (RuntimeException e) {
            captureFailure("Click '" + text + "'", text, e);
            throw e;
        }
    }

    public void fill(String selector, String value) {
        fill(selector, value, "Enter text in " + humanize(selector));
    }

    /** As {@link #fill(String, String)} but with a caller-supplied report description. */
    public void fill(String selector, String value, String description) {
        UiExtentReportManager.logStepWithLocator(description, selector);
        try {
            page.locator(selector).fill(value);
        } catch (RuntimeException e) {
            captureFailure(description, selector, e);
            throw e;
        }
    }

    public void pressKey(String selector, String key) {
        String description = "Press " + key + " on " + humanize(selector);
        UiExtentReportManager.logStepWithLocator(description, selector);
        try {
            page.locator(selector).press(key);
        } catch (RuntimeException e) {
            captureFailure(description, selector, e);
            throw e;
        }
    }

    public void selectDropdownByText(String triggerSelector, String optionText) {
        String description = "Select '" + optionText + "' in " + humanize(triggerSelector);
        UiExtentReportManager.logStepWithLocator(description, triggerSelector);
        try {
            page.locator(triggerSelector).click();
            page.getByText(optionText).first().click();
        } catch (RuntimeException e) {
            captureFailure(description, triggerSelector, e);
            throw e;
        }
    }

    /** Captures a screenshot (best-effort) and logs it with the failure, synchronously on
     *  the scenario's own thread for an accurate position and time in the report. */
    private void captureFailure(String description, String locator, Throwable error) {
        String screenshotBase64 = null;
        try {
            screenshotBase64 = takeScreenshotBase64();
        } catch (Exception e) {
            logger.warn("Could not capture failure screenshot: {}", e.getMessage());
        }
        UiExtentReportManager.logFailureWithScreenshot(description, locator, error, screenshotBase64);
    }

    // ── Report label helper ────────────────────────────────────────────────────

    /**
     * Derives a human-readable label from a selector for the report description.
     * Simple id/class selectors (e.g. {@code #regCenterSearchBtn}, {@code .search-box})
     * become {@code "reg center search btn"} / {@code "search box"} by splitting
     * camelCase, snake_case and kebab-case. Complex CSS/XPath selectors are returned
     * verbatim — the full selector is always shown in the collapsible Locator Details.
     */
    protected static String humanize(String selector) {
        if (selector == null || selector.isBlank()) return "element";
        String s = selector.trim();
        if (!s.matches("[#.][A-Za-z0-9_-]+")) {
            return selector;   // not a simple id/class — keep raw selector
        }
        s = s.substring(1)
             .replaceAll("([a-z0-9])([A-Z])", "$1 $2")
             .replace('_', ' ')
             .replace('-', ' ');
        return s.toLowerCase().trim();
    }

    // ── Self-healing interactions ─────────────────────────────────────────────
    // Use when a selector is likely to change and you know the fallback alternatives.

    /**
     * Clicks the first selector (primary or fallbacks) that resolves to an element.
     * Logs a warning to the Extent report if a fallback was used.
     */
    public void clickHealing(String primary, String... fallbacks) {
        UiExtentReportManager.logStepWithLocator("Click " + humanize(primary) + " (self-healing)", primary);
        SelfHealingLocator.click(page, primary, fallbacks);
    }

    /**
     * Fills the first selector (primary or fallbacks) that resolves to an element.
     */
    public void fillHealing(String value, String primary, String... fallbacks) {
        UiExtentReportManager.logStepWithLocator(
                "Enter text in " + humanize(primary) + " (self-healing)", primary);
        SelfHealingLocator.fill(page, value, primary, fallbacks);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    public String getText(String selector) {
        return page.locator(selector).textContent();
    }

    public int count(String selector) {
        return page.locator(selector).count();
    }

    // ── File upload ───────────────────────────────────────────────────────────

    public void uploadFile(String selector, String absoluteFilePath) {
        page.setInputFiles(selector, Paths.get(absoluteFilePath));
    }

    // ── Popup helpers ─────────────────────────────────────────────────────────

    public void dismissPopupIfPresent(String dismissSelector) {
        // Best-effort: isVisible() is a snapshot, so the popup can vanish before click() runs —
        // cap the timeout short and swallow failures rather than inheriting the default 30s wait.
        try {
            Locator btn = page.locator(dismissSelector);
            if (btn.isVisible()) btn.click(new Locator.ClickOptions().setTimeout(3_000));
        } catch (Exception e) {
            logger.debug("dismissPopupIfPresent({}) found nothing to dismiss: {}", dismissSelector, e.getMessage());
        }
    }

    // ── Element factory methods ───────────────────────────────────────────────
    // Use typed elements in page objects for richer, self-documenting interactions.

    protected Button button(String selector) {
        return Button.from(page, selector);
    }

    protected TextBox textBox(String selector) {
        return TextBox.from(page, selector);
    }

    protected DropDown dropDown(String selector) {
        return DropDown.from(page, selector);
    }

    protected DatePicker datePicker(String selector) {
        return DatePicker.from(page, selector);
    }

    protected Table table(String selector) {
        return Table.from(page, selector);
    }

    protected Table matTable(String selector) {
        return Table.matTable(page, selector);
    }

    protected Toast toast(String selector) {
        return Toast.from(page, selector);
    }

    protected Dialog dialog(String selector) {
        return Dialog.from(page, selector);
    }

    // ── Assertion factory methods ─────────────────────────────────────────────

    /**
     * Returns a rich assertion library bound to the current page.
     * Assertions throw immediately on failure with descriptive messages.
     */
    protected AssertionLibrary assertions() {
        return new AssertionLibrary(page);
    }

    /**
     * Returns a soft assertion collector bound to the current page.
     * Failures accumulate until {@code assertAll()} is called.
     */
    protected SoftAssertions softAssertions() {
        return new SoftAssertions(page);
    }

    // ── Visual diff factory ───────────────────────────────────────────────────

    /**
     * Returns a {@link VisualDiffEngine} bound to this page, with no masks and the
     * default tolerance.  Chain {@code maskRegion()} and {@code withTolerance()} before
     * calling {@code assertPageSnapshot()} or {@code assertElementSnapshot()}.
     *
     * <pre>
     *   visualDiff()
     *       .maskRegion("#requestTimestamp")
     *       .maskRegion(".user-event-count")
     *       .assertPageSnapshot("view-history.png");
     * </pre>
     */
    protected VisualDiffEngine visualDiff() {
        return new VisualDiffEngine(page);
    }

    // ── Accessibility factory ─────────────────────────────────────────────────

    /**
     * Asserts no WCAG 2.1 AA accessibility violations on the current page.
     * Convenience delegation to {@link AccessibilityValidator#assertNoViolations(Page)}.
     */
    protected void assertNoAccessibilityViolations() {
        AccessibilityValidator.assertNoViolations(page);
    }

    /**
     * Asserts no violations at the specified WCAG level, excluding the listed rule IDs.
     */
    protected void assertNoAccessibilityViolations(WcagLevel level, java.util.List<String> excludedRules) {
        AccessibilityValidator.assertNoViolations(page, level, excludedRules);
    }

    /**
     * Asserts only {@code critical}/{@code serious} violations fail the test;
     * minor/moderate violations are logged but do not cause a failure.
     */
    protected void assertNoBlockerAccessibilityViolations(WcagLevel level) {
        AccessibilityValidator.assertNoBlockerViolations(page, level);
    }

    // ── Screenshots ───────────────────────────────────────────────────────────

    public String takeScreenshotBase64() {
        return Base64.getEncoder().encodeToString(
                page.screenshot(new Page.ScreenshotOptions().setFullPage(true)));
    }

    public void saveScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(path))
                .setFullPage(true));
    }
}
