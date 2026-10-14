package io.mosip.testrig.uitestrig.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Typed wrapper for modal dialog elements (confirmation dialogs, warning dialogs).
 *
 * Supports Angular Material {@code mat-dialog} containers and generic dialog patterns.
 * Assumes the dialog has a title, a confirm action, and a cancel/dismiss action.
 *
 * <pre>
 *   Dialog confirmDialog = Dialog.from(page, "mat-dialog-container");
 *   confirmDialog.waitUntilOpen();
 *   String title = confirmDialog.getTitle();
 *   confirmDialog.confirm();   // clicks the primary/OK button
 *
 *   // Or dismiss it
 *   confirmDialog.dismiss();   // clicks the cancel/No button
 * </pre>
 */
public class Dialog extends UiElement {

    private final Page page;
    private final String confirmSelector;
    private final String dismissSelector;
    private final String titleSelector;

    private Dialog(Page page, Locator locator,
                   String titleSelector, String confirmSelector, String dismissSelector) {
        super(locator);
        this.page = page;
        this.titleSelector = titleSelector;
        this.confirmSelector = confirmSelector;
        this.dismissSelector = dismissSelector;
    }

    /**
     * Creates a Dialog with Angular Material default selectors:
     * <ul>
     *   <li>Title: {@code mat-dialog-title} or {@code h2} inside the container</li>
     *   <li>Confirm: {@code [cdkfocusinitial], button[color='primary']}</li>
     *   <li>Dismiss: {@code button[mat-dialog-close], button:has-text('Cancel'), button:has-text('No')}</li>
     * </ul>
     */
    public static Dialog from(Page page, String containerSelector) {
        return new Dialog(
                page,
                page.locator(containerSelector),
                "mat-dialog-title, h2",
                "[cdkfocusinitial], button[color='primary']",
                "button[mat-dialog-close], button:has-text('Cancel'), button:has-text('No')"
        );
    }

    /**
     * Creates a Dialog with explicit selectors for title, confirm, and dismiss buttons.
     * Use this when the dialog structure doesn't follow Angular Material conventions.
     */
    public static Dialog custom(Page page, String containerSelector,
                                String titleSelector, String confirmSelector, String dismissSelector) {
        return new Dialog(page, page.locator(containerSelector),
                titleSelector, confirmSelector, dismissSelector);
    }

    /** Waits until the dialog becomes visible. */
    public void waitUntilOpen() {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10_000));
    }

    /** Waits until the dialog closes. */
    public void waitUntilClosed() {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(10_000));
    }

    /** Returns the dialog title text. */
    public String getTitle() {
        Locator title = locator.locator(titleSelector);
        return title.isVisible() ? title.textContent().trim() : "";
    }

    /** Returns the full dialog body text. */
    public String getBodyText() {
        return locator.textContent().trim();
    }

    /** Clicks the confirm/primary action button (OK, Yes, Submit, etc.). */
    public void confirm() {
        locator.locator(confirmSelector).first().click();
    }

    /** Clicks the cancel/secondary action button (Cancel, No, Close). */
    public void dismiss() {
        locator.locator(dismissSelector).first().click();
    }

    /** Closes the dialog by pressing Escape. */
    public void pressEscape() {
        page.keyboard().press("Escape");
    }
}
