package io.mosip.testrig.uitestrig.browser;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.BooleanSupplier;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Smart wait utilities that replace Thread.sleep with deterministic conditions.
 *
 * All methods use Playwright's built-in polling — no busy loops, no arbitrary sleeps.
 * Default timeout for each method is 30 seconds unless overridden.
 */
public class SmartWait {

    private static final Logger logger = LoggerFactory.getLogger(SmartWait.class);
    private static final int DEFAULT_TIMEOUT_MS = 30_000;

    private SmartWait() {}

    /**
     * Waits until the Angular NgZone is stable (no pending microtasks or macrotasks).
     * Falls back gracefully if the page is not an Angular app.
     */
    public static void forAngular(Page page) {
        try {
            page.waitForFunction(
                "() => { " +
                "  if (!window.getAllAngularTestabilities) return true; " +
                "  return window.getAllAngularTestabilities().every(t => t.isStable()); " +
                "}",
                null,
                new Page.WaitForFunctionOptions().setTimeout(DEFAULT_TIMEOUT_MS)
            );
            logger.debug("Angular NgZone stable");
        } catch (Exception e) {
            logger.debug("Angular stability wait skipped or timed out: {}", e.getMessage());
        }
    }

    /**
     * Waits until the network is idle (no requests for 500ms).
     * Equivalent to Playwright's NETWORKIDLE load state.
     */
    public static void forNetworkIdle(Page page) {
        page.waitForLoadState(LoadState.NETWORKIDLE,
                new Page.WaitForLoadStateOptions().setTimeout(DEFAULT_TIMEOUT_MS));
    }

    /**
     * Waits until a spinner/loader element is no longer visible.
     * Useful for pages that show a loading indicator after navigation.
     *
     * @param spinnerSelector CSS selector for the spinner element
     */
    public static void forSpinnerToDisappear(Page page, String spinnerSelector) {
        Locator spinner = page.locator(spinnerSelector);
        if (spinner.count() > 0 && spinner.first().isVisible()) {
            spinner.first().waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.HIDDEN)
                    .setTimeout(DEFAULT_TIMEOUT_MS));
            logger.debug("Spinner disappeared: {}", spinnerSelector);
        }
    }

    /**
     * Waits until a locator contains the expected text.
     */
    public static void forText(Locator locator, String text) {
        assertThat(locator).containsText(text,
                new com.microsoft.playwright.assertions.LocatorAssertions.ContainsTextOptions()
                        .setTimeout(DEFAULT_TIMEOUT_MS));
    }

    /**
     * Waits until a locator matches exactly the expected count.
     */
    public static void forCount(Locator locator, int expected) {
        assertThat(locator).hasCount(expected,
                new com.microsoft.playwright.assertions.LocatorAssertions.HasCountOptions()
                        .setTimeout(DEFAULT_TIMEOUT_MS));
    }

    /**
     * Waits until a locator becomes enabled (not disabled).
     */
    public static void forEnabled(Locator locator) {
        assertThat(locator).isEnabled(
                new com.microsoft.playwright.assertions.LocatorAssertions.IsEnabledOptions()
                        .setTimeout(DEFAULT_TIMEOUT_MS));
    }

    /**
     * Waits until a locator becomes visible.
     */
    public static void forVisible(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(DEFAULT_TIMEOUT_MS));
    }

    /**
     * Waits until all CSS transitions and animations on the page have completed.
     * Useful before taking screenshots to avoid capturing mid-animation states.
     */
    public static void forAnimation(Page page) {
        try {
            page.waitForFunction(
                "() => { " +
                "  const els = document.querySelectorAll('*'); " +
                "  for (const el of els) { " +
                "    const style = getComputedStyle(el); " +
                "    if (style.animationName !== 'none' || parseFloat(style.transitionDuration) > 0) " +
                "      return false; " +
                "  } " +
                "  return true; " +
                "}",
                null,
                new Page.WaitForFunctionOptions().setTimeout(5_000)
            );
        } catch (Exception e) {
            // Animation wait is best-effort; don't fail the test
            logger.debug("Animation wait skipped: {}", e.getMessage());
        }
    }

    /**
     * Polls a condition until it returns true or the timeout expires.
     * Prefer Playwright-native waits when possible; use this as a last resort.
     *
     * @throws AssertionError if the condition is not met within the timeout
     */
    public static void untilTrue(BooleanSupplier condition, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        long interval = 200;
        while (System.currentTimeMillis() < deadline) {
            if (condition.getAsBoolean()) return;
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (!condition.getAsBoolean()) {
            throw new AssertionError("Condition not met within " + timeout.toSeconds() + "s");
        }
    }
}
