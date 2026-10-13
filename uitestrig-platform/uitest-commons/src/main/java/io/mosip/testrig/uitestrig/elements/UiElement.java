package io.mosip.testrig.uitestrig.elements;

import com.microsoft.playwright.Locator;

/**
 * Base class for all typed UI elements.
 * Holds the resolved Playwright Locator and provides common predicates.
 */
public abstract class UiElement {

    protected final Locator locator;

    protected UiElement(Locator locator) {
        this.locator = locator;
    }

    /** Returns whether this element is currently visible in the DOM. */
    public boolean isVisible() {
        return locator.isVisible();
    }

    /** Returns whether this element exists in the DOM (count > 0). */
    public boolean exists() {
        return locator.count() > 0;
    }

    /** Returns whether this element is enabled (not disabled). */
    public boolean isEnabled() {
        return locator.isEnabled();
    }

    /** Waits until this element is visible, up to Playwright's default timeout. */
    public void waitUntilVisible() {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
    }

    /** Waits until this element is hidden or removed, up to Playwright's default timeout. */
    public void waitUntilHidden() {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN));
    }

    /** Exposes the underlying Playwright Locator for advanced interactions not covered by this API. */
    public Locator locator() {
        return locator;
    }
}
