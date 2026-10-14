package io.mosip.testrig.uitestrig.assertions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import io.mosip.testrig.uitestrig.core.UiGlobalConstants;
import io.mosip.testrig.uitestrig.reporting.UiExtentReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Element-level visual assertions using Playwright's built-in assertThat API.
 * These run on every test execution (not snapshot-based).
 */
public class VisualValidator {

    private static final Logger logger = LoggerFactory.getLogger(VisualValidator.class);

    private final Page page;

    public VisualValidator(Page page) {
        this.page = page;
    }

    public void assertVisible(String selector) {
        assertThat(page.locator(selector)).isVisible();
        UiExtentReportManager.logPass("Verified '" + selector + "' is visible");
    }

    public void assertNotVisible(String selector) {
        assertThat(page.locator(selector)).not().isVisible();
        UiExtentReportManager.logPass("Verified '" + selector + "' is not visible");
    }

    public void assertHasText(String selector, String expectedText) {
        assertThat(page.locator(selector)).hasText(expectedText);
        UiExtentReportManager.logPass("Verified '" + selector + "' has text \"" + expectedText + "\"");
    }

    public void assertContainsText(String selector, String partialText) {
        assertThat(page.locator(selector)).containsText(partialText);
        UiExtentReportManager.logPass("Verified '" + selector + "' contains text \"" + partialText + "\"");
    }

    public void assertCount(String selector, int expectedCount) {
        assertThat(page.locator(selector)).hasCount(expectedCount);
        UiExtentReportManager.logPass("Verified '" + selector + "' count is " + expectedCount);
    }

    public void assertHasValue(String selector, String value) {
        assertThat(page.locator(selector)).hasValue(value);
        UiExtentReportManager.logPass("Verified '" + selector + "' has value \"" + value + "\"");
    }

    public void assertEnabled(String selector) {
        assertThat(page.locator(selector)).isEnabled();
        UiExtentReportManager.logPass("Verified '" + selector + "' is enabled");
    }

    public void assertDisabled(String selector) {
        assertThat(page.locator(selector)).isDisabled();
        UiExtentReportManager.logPass("Verified '" + selector + "' is disabled");
    }

    public void assertChecked(String selector) {
        assertThat(page.locator(selector)).isChecked();
        UiExtentReportManager.logPass("Verified '" + selector + "' is checked");
    }

    /** Validates that the dismiss/close button is present after an action.
     *  Uses 30s timeout — success popups may appear after an async API response. */
    public void assertSuccessPopup() {
        assertThat(page.locator(UiGlobalConstants.DISMISS_BTN))
                .isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(30_000));
        logger.debug("Success popup confirmed visible");
        UiExtentReportManager.logPass("Success popup displayed");
    }
}
