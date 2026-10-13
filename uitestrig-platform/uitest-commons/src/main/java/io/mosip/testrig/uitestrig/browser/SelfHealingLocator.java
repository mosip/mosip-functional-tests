package io.mosip.testrig.uitestrig.browser;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.mosip.testrig.uitestrig.reporting.UiExtentReportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tier 1 self-healing locator — rule-based, zero cost, no LLM.
 *
 * When the primary selector resolves to 0 elements, each fallback selector is tried
 * in order. The first one that resolves to at least one element is used and the healing
 * event is logged to both SLF4J and the Extent report.
 *
 * Handles ~80% of locator breakage caused by ID renames, class changes, or minor
 * structural refactors.
 *
 * Usage in page objects (explicit fallback declaration):
 * <pre>
 *   // Primary ID + data-testid fallback + text fallback
 *   click(SelfHealingLocator.resolve(page, "#loginBtn",
 *                                         "[data-testid='login-button']",
 *                                         "button:has-text('Login')"));
 *
 *   // Or via BasePage helpers that wrap this automatically
 *   clickHealing("#loginBtn", "[data-testid='login-button']");
 * </pre>
 *
 * Healed selectors are reported in the Extent report as warnings — a human should
 * update the source selector to prevent repeated healing.
 */
public class SelfHealingLocator {

    private static final Logger logger = LoggerFactory.getLogger(SelfHealingLocator.class);

    private SelfHealingLocator() {}

    /**
     * Returns the first locator (from primary + fallbacks) that resolves to at least one element.
     *
     * @param page      current Playwright page
     * @param primary   the preferred selector (tried first)
     * @param fallbacks alternative selectors tried in order if primary fails
     * @return a Playwright Locator pointing to the matched element
     * @throws IllegalStateException if no selector resolves to any element
     */
    public static Locator resolve(Page page, String primary, String... fallbacks) {
        Locator locator = page.locator(primary);
        if (locator.count() > 0) {
            return locator;
        }

        for (String fallback : fallbacks) {
            Locator candidate = page.locator(fallback);
            if (candidate.count() > 0) {
                String msg = "⚠️ Healed: '" + primary + "' → '" + fallback + "'";
                logger.warn(msg);
                UiExtentReportManager.logStep(msg);
                return candidate;
            }
        }

        throw new IllegalStateException(
            "SelfHealingLocator: no selector resolved. Tried: [" + primary + ", " +
            String.join(", ", fallbacks) + "]");
    }

    /**
     * Clicks the first resolving selector (primary or fallback).
     */
    public static void click(Page page, String primary, String... fallbacks) {
        resolve(page, primary, fallbacks).click();
    }

    /**
     * Fills the first resolving selector with the given value.
     */
    public static void fill(Page page, String value, String primary, String... fallbacks) {
        resolve(page, primary, fallbacks).fill(value);
    }

    /**
     * Returns true if any of the provided selectors resolve to at least one element.
     */
    public static boolean isVisible(Page page, String primary, String... fallbacks) {
        if (page.locator(primary).count() > 0 && page.locator(primary).first().isVisible()) {
            return true;
        }
        for (String fallback : fallbacks) {
            Locator candidate = page.locator(fallback);
            if (candidate.count() > 0 && candidate.first().isVisible()) return true;
        }
        return false;
    }
}
