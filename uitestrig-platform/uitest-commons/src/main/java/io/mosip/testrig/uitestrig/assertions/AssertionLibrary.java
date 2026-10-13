package io.mosip.testrig.uitestrig.assertions;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import io.mosip.testrig.uitestrig.elements.Table;
import io.mosip.testrig.uitestrig.reporting.UiExtentReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Rich assertion library for MOSIP UI tests.
 *
 * Extends the basic {@link VisualValidator} with:
 * <ul>
 *   <li>Self-documenting failure messages that include actual vs. expected values</li>
 *   <li>URL and page title assertions</li>
 *   <li>CSS computed-style (color) assertions</li>
 *   <li>HTML attribute assertions</li>
 *   <li>Table data comparison with row-level diff output</li>
 *   <li>Download verification</li>
 * </ul>
 *
 * Use {@link SoftAssertions} to collect multiple assertion failures and throw them together.
 *
 * <pre>
 *   AssertionLibrary assert = new AssertionLibrary(page);
 *   assert.verifyText("#header", "Manage My VID");
 *   assert.verifyURLContains("/managemyvid");
 *   assert.verifyColor("#submitBtn", "background-color", "#1976d2");
 * </pre>
 */
public class AssertionLibrary {

    private static final Logger logger = LoggerFactory.getLogger(AssertionLibrary.class);
    private static final int COLOR_TOLERANCE = 5;   // per-channel tolerance for CSS color comparison

    private final Page page;

    public AssertionLibrary(Page page) {
        this.page = page;
    }

    // ── Visibility / State ─────────────────────────────────────────────────────

    public void verifyVisible(String selector) {
        assertThat(page.locator(selector))
                .isVisible(new com.microsoft.playwright.assertions.LocatorAssertions.IsVisibleOptions()
                        .setTimeout(10_000));
        UiExtentReportManager.logPass("Verified '" + selector + "' is visible");
    }

    public void verifyNotVisible(String selector) {
        assertThat(page.locator(selector))
                .not().isVisible();
        UiExtentReportManager.logPass("Verified '" + selector + "' is not visible");
    }

    public void verifyEnabled(String selector) {
        assertThat(page.locator(selector)).isEnabled();
        UiExtentReportManager.logPass("Verified '" + selector + "' is enabled");
    }

    public void verifyDisabled(String selector) {
        assertThat(page.locator(selector)).isDisabled();
        UiExtentReportManager.logPass("Verified '" + selector + "' is disabled");
    }

    // ── Text ───────────────────────────────────────────────────────────────────

    /**
     * Verifies the element's full text content matches exactly (ignoring surrounding whitespace).
     */
    public void verifyText(String selector, String expected) {
        String actual = page.locator(selector).textContent();
        if (actual == null || !actual.trim().equals(expected.trim())) {
            throw new AssertionError(
                "Text mismatch for '" + selector + "':\n" +
                "  Expected: \"" + expected + "\"\n" +
                "  Actual:   \"" + (actual != null ? actual.trim() : "<null>") + "\"");
        }
        UiExtentReportManager.logPass("Verified '" + selector + "' text is \"" + expected + "\"");
    }

    /**
     * Verifies the element's text content contains the given substring.
     */
    public void verifyPartialText(String selector, String contains) {
        String actual = page.locator(selector).textContent();
        if (actual == null || !actual.contains(contains)) {
            throw new AssertionError(
                "Partial text not found in '" + selector + "':\n" +
                "  Expected to contain: \"" + contains + "\"\n" +
                "  Actual text:         \"" + (actual != null ? actual.trim() : "<null>") + "\"");
        }
        UiExtentReportManager.logPass("Verified '" + selector + "' contains text \"" + contains + "\"");
    }

    /**
     * Verifies an input/textarea field's current value.
     */
    public void verifyInputValue(String selector, String expected) {
        String actual = page.locator(selector).inputValue();
        if (!expected.equals(actual)) {
            throw new AssertionError(
                "Input value mismatch for '" + selector + "':\n" +
                "  Expected: \"" + expected + "\"\n" +
                "  Actual:   \"" + actual + "\"");
        }
        UiExtentReportManager.logPass("Verified '" + selector + "' input value is \"" + expected + "\"");
    }

    // ── Count ──────────────────────────────────────────────────────────────────

    /**
     * Verifies the number of elements matching the selector.
     */
    public void verifyCount(String selector, int expected) {
        int actual = page.locator(selector).count();
        if (actual != expected) {
            throw new AssertionError(
                "Element count mismatch for '" + selector + "':\n" +
                "  Expected: " + expected + "\n" +
                "  Actual:   " + actual);
        }
        UiExtentReportManager.logPass("Verified '" + selector + "' count is " + expected);
    }

    // ── URL & Title ────────────────────────────────────────────────────────────

    /**
     * Verifies the current page URL matches the given pattern exactly.
     * Pass a regex string or a literal URL fragment — uses {@code String.matches()} semantics.
     * For a simple substring check, use {@link #verifyURLContains(String)}.
     */
    public void verifyURL(String pattern) {
        assertThat(page).hasURL(Pattern.compile(pattern));
        UiExtentReportManager.logPass("Verified URL matches \"" + pattern + "\"");
    }

    /**
     * Verifies the current page URL contains the given fragment.
     */
    public void verifyURLContains(String fragment) {
        String actual = page.url();
        if (!actual.contains(fragment)) {
            throw new AssertionError(
                "URL does not contain expected fragment:\n" +
                "  Expected fragment: \"" + fragment + "\"\n" +
                "  Actual URL:        \"" + actual + "\"");
        }
        UiExtentReportManager.logPass("Verified URL contains \"" + fragment + "\"");
    }

    /**
     * Verifies the page title matches the given text exactly.
     */
    public void verifyTitle(String expected) {
        assertThat(page).hasTitle(expected);
        UiExtentReportManager.logPass("Verified page title is \"" + expected + "\"");
    }

    /**
     * Verifies the page title contains the given substring.
     */
    public void verifyTitleContains(String partial) {
        String actual = page.title();
        if (!actual.contains(partial)) {
            throw new AssertionError(
                "Page title does not contain expected text:\n" +
                "  Expected to contain: \"" + partial + "\"\n" +
                "  Actual title:        \"" + actual + "\"");
        }
        UiExtentReportManager.logPass("Verified page title contains \"" + partial + "\"");
    }

    // ── Attributes ─────────────────────────────────────────────────────────────

    /**
     * Verifies an HTML attribute equals the expected value exactly.
     */
    public void verifyAttributeValue(String selector, String attr, String expected) {
        String actual = page.locator(selector).getAttribute(attr);
        if (!expected.equals(actual)) {
            throw new AssertionError(
                "Attribute mismatch for '" + selector + "' [" + attr + "]:\n" +
                "  Expected: \"" + expected + "\"\n" +
                "  Actual:   \"" + actual + "\"");
        }
        UiExtentReportManager.logPass("Verified '" + selector + "' attribute [" + attr + "] is \"" + expected + "\"");
    }

    /**
     * Verifies an HTML attribute contains the expected substring.
     */
    public void verifyAttributeContains(String selector, String attr, String fragment) {
        String actual = page.locator(selector).getAttribute(attr);
        if (actual == null || !actual.contains(fragment)) {
            throw new AssertionError(
                "Attribute '" + attr + "' of '" + selector + "' does not contain expected fragment:\n" +
                "  Expected to contain: \"" + fragment + "\"\n" +
                "  Actual value:        \"" + actual + "\"");
        }
        UiExtentReportManager.logPass("Verified '" + selector + "' attribute [" + attr + "] contains \"" + fragment + "\"");
    }

    // ── CSS Color ──────────────────────────────────────────────────────────────

    /**
     * Verifies a computed CSS color property matches the expected hex color.
     *
     * Browsers return computed colors as {@code rgb(r, g, b)} or {@code rgba(r, g, b, a)}.
     * Comparison uses a per-channel tolerance of ±{@value COLOR_TOLERANCE} to handle
     * minor rendering differences between browsers.
     *
     * @param selector     CSS selector for the element
     * @param cssProperty  CSS property name, e.g. {@code "background-color"}, {@code "color"}
     * @param expectedHex  Expected color as hex string, e.g. {@code "#1976d2"} or {@code "1976d2"}
     */
    public void verifyColor(String selector, String cssProperty, String expectedHex) {
        Object rawValue = page.evaluate(
            "([sel, prop]) => { " +
            "  const el = document.querySelector(sel); " +
            "  return el ? getComputedStyle(el)[prop] : null; " +
            "}",
            List.of(selector, cssProperty)
        );

        if (rawValue == null) {
            throw new AssertionError(
                "Could not read CSS property '" + cssProperty + "' for selector '" + selector + "'");
        }

        String actualCss = rawValue.toString();
        int[] actual   = parseRgbFromCss(actualCss);
        int[] expected = hexToRgb(expectedHex);

        if (actual == null) {
            throw new AssertionError(
                "Could not parse computed CSS color value '" + actualCss +
                "' for selector '" + selector + "' property '" + cssProperty + "'");
        }

        if (!colorsMatch(actual, expected)) {
            throw new AssertionError(
                "Color mismatch for '" + selector + "' CSS property '" + cssProperty + "':\n" +
                "  Expected: " + expectedHex + " → rgb(" + expected[0] + "," + expected[1] + "," + expected[2] + ")\n" +
                "  Actual:   " + actualCss);
        }
        logger.debug("Color verified — {}: {} matches {}", selector, actualCss, expectedHex);
        UiExtentReportManager.logPass("Verified '" + selector + "' " + cssProperty + " is " + expectedHex);
    }

    // ── Table ──────────────────────────────────────────────────────────────────

    /**
     * Verifies that a table's data rows match the expected 2D list exactly.
     * Produces a row-level diff in the failure message.
     *
     * @param tableSelector CSS selector for the {@code <table>} or {@code mat-table} element
     * @param expectedData  expected table contents as rows × cells
     */
    public void verifyTable(String tableSelector, List<List<String>> expectedData) {
        List<List<String>> actualData = Table.from(page, tableSelector).getAllData();

        if (actualData.equals(expectedData)) {
            UiExtentReportManager.logPass("Verified table '" + tableSelector + "' matches expected data ("
                    + expectedData.size() + " rows)");
            return;
        }

        StringBuilder diff = new StringBuilder(
            "Table data mismatch for '" + tableSelector + "':\n");
        int rows = Math.max(actualData.size(), expectedData.size());
        for (int i = 0; i < rows; i++) {
            List<String> actual   = i < actualData.size()   ? actualData.get(i)   : List.of("<missing row>");
            List<String> expected = i < expectedData.size() ? expectedData.get(i) : List.of("<extra row>");
            if (!actual.equals(expected)) {
                diff.append("  Row ").append(i).append(":\n")
                    .append("    Expected: ").append(expected).append("\n")
                    .append("    Actual:   ").append(actual).append("\n");
            }
        }
        throw new AssertionError(diff.toString());
    }

    // ── Download ───────────────────────────────────────────────────────────────

    /**
     * Triggers an action, waits for the resulting file download, and returns the Download handle.
     * Use when you need to inspect the downloaded file path or content.
     *
     * @param triggerAction lambda that triggers the download (e.g. {@code () -> click("#downloadBtn")})
     */
    public Download awaitDownload(Runnable triggerAction) {
        return page.waitForDownload(triggerAction::run);
    }

    /**
     * Triggers an action, waits for the download, and verifies the filename.
     *
     * @param expectedFileNamePattern  exact filename or a substring to search for within the filename
     * @param triggerAction            lambda that triggers the download
     */
    public void verifyDownload(String expectedFileNamePattern, Runnable triggerAction) {
        Download download = page.waitForDownload(triggerAction::run);
        String actualName = download.suggestedFilename();
        if (!actualName.contains(expectedFileNamePattern) && !actualName.matches(expectedFileNamePattern)) {
            throw new AssertionError(
                "Download filename mismatch:\n" +
                "  Expected (pattern): \"" + expectedFileNamePattern + "\"\n" +
                "  Actual filename:    \"" + actualName + "\"");
        }
        logger.debug("Download verified: {}", actualName);
        UiExtentReportManager.logPass("Verified download filename: \"" + actualName + "\"");
    }

    // ── Internals ──────────────────────────────────────────────────────────────

    private static int[] parseRgbFromCss(String css) {
        Matcher m = Pattern.compile("rgba?\\((\\d+),\\s*(\\d+),\\s*(\\d+)").matcher(css);
        if (!m.find()) return null;
        return new int[]{
            Integer.parseInt(m.group(1)),
            Integer.parseInt(m.group(2)),
            Integer.parseInt(m.group(3))
        };
    }

    private static int[] hexToRgb(String hex) {
        String clean = hex.startsWith("#") ? hex.substring(1) : hex;
        if (clean.length() == 3) {
            clean = "" + clean.charAt(0) + clean.charAt(0)
                       + clean.charAt(1) + clean.charAt(1)
                       + clean.charAt(2) + clean.charAt(2);
        }
        return new int[]{
            Integer.parseInt(clean.substring(0, 2), 16),
            Integer.parseInt(clean.substring(2, 4), 16),
            Integer.parseInt(clean.substring(4, 6), 16)
        };
    }

    private static boolean colorsMatch(int[] actual, int[] expected) {
        return Math.abs(actual[0] - expected[0]) <= COLOR_TOLERANCE
            && Math.abs(actual[1] - expected[1]) <= COLOR_TOLERANCE
            && Math.abs(actual[2] - expected[2]) <= COLOR_TOLERANCE;
    }
}
