package io.mosip.testrig.uitestrig.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Typed wrapper for toast / notification popup elements.
 *
 * In the MOSIP Resident portal, the success popup has a dismiss button ({@code #dismissBtn})
 * and a message area. This class wraps those interactions cleanly.
 *
 * <pre>
 *   Toast successToast = Toast.from(page, ".mosip-snack-bar");
 *   successToast.waitForToast();
 *   String msg = successToast.getMessage();
 *   successToast.dismiss("#dismissBtn");
 *
 *   // For the MOSIP dismiss-button pattern
 *   Toast popup = Toast.mosipPopup(page);
 *   popup.waitForToast();
 *   popup.dismissWithButton();
 * </pre>
 */
public class Toast extends UiElement {

    private static final String MOSIP_DISMISS_BTN = "#dismissBtn";
    private static final String MOSIP_MESSAGE_POPUP = "#messagePopup";

    private Toast(Locator locator) {
        super(locator);
    }

    /** Creates a Toast from a CSS selector targeting the toast/snackbar container. */
    public static Toast from(Page page, String selector) {
        return new Toast(page.locator(selector));
    }

    /** Creates a Toast for the standard MOSIP Resident Portal popup pattern. */
    public static Toast mosipPopup(Page page) {
        return new Toast(page.locator(MOSIP_MESSAGE_POPUP));
    }

    /**
     * Waits until the toast becomes visible.
     * Useful when the toast appears asynchronously after an action.
     */
    public void waitForToast() {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15_000));
    }

    /**
     * Waits until the toast disappears (auto-dismiss or after user action).
     */
    public void waitUntilDismissed() {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(15_000));
    }

    /**
     * Returns the visible text message from the toast container.
     */
    public String getMessage() {
        return locator.textContent().trim();
    }

    /**
     * Returns true if the toast is no longer visible (has been dismissed or auto-hidden).
     */
    public boolean isDismissed() {
        return !locator.isVisible();
    }

    /**
     * Clicks the dismiss button inside the toast.
     *
     * @param dismissButtonSelector CSS selector for the close/dismiss button
     */
    public void dismiss(String dismissButtonSelector) {
        locator.locator(dismissButtonSelector).click();
    }

    /**
     * Clicks the standard MOSIP Resident Portal dismiss button ({@code #dismissBtn}).
     */
    public void dismissWithButton() {
        locator.page().locator(MOSIP_DISMISS_BTN).click();
    }
}
