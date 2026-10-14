package io.mosip.testrig.uitestrig.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Typed wrapper for text input and textarea elements.
 *
 * <pre>
 *   TextBox otpInput = TextBox.from(page, "#Otp_mosip-vid");
 *   otpInput.fill("123456");
 *
 *   TextBox searchBox = TextBox.byPlaceholder(page, "Search...");
 *   searchBox.fill("test query");
 * </pre>
 */
public class TextBox extends UiElement {

    private TextBox(Locator locator) {
        super(locator);
    }

    /** Creates a TextBox from a CSS/XPath selector. */
    public static TextBox from(Page page, String selector) {
        return new TextBox(page.locator(selector));
    }

    /** Creates a TextBox matched by its placeholder attribute. */
    public static TextBox byPlaceholder(Page page, String placeholder) {
        return new TextBox(page.getByPlaceholder(placeholder));
    }

    /** Creates a TextBox matched by its associated label text. */
    public static TextBox byLabel(Page page, String labelText) {
        return new TextBox(page.getByLabel(labelText));
    }

    /**
     * Clears any existing content and types the new value.
     * Uses Playwright's fill() which is atomic — no character-by-character events.
     */
    public void fill(String value) {
        locator.fill(value);
    }

    /**
     * Types value character by character, triggering keydown/keypress/keyup events.
     * Use this when the field has auto-complete or key-event listeners that fill() bypasses.
     */
    public void type(String value) {
        locator.pressSequentially(value);
    }

    /** Clears the field content. */
    public void clear() {
        locator.clear();
    }

    /** Returns the current value of the input field. */
    public String getValue() {
        return locator.inputValue();
    }

    /** Returns true if the field is empty. */
    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    /** Presses a key on the field (e.g. "Enter", "Tab", "Escape"). */
    public void press(String key) {
        locator.press(key);
    }
}
