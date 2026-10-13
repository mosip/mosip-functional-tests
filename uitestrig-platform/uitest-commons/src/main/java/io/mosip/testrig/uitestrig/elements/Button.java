package io.mosip.testrig.uitestrig.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Typed wrapper for button and link elements.
 *
 * <pre>
 *   Button loginBtn = Button.from(page, "#login_with_otp");
 *   loginBtn.click();
 *
 *   // Also works with ARIA role — more resilient than CSS ID
 *   Button submit = Button.byRole(page, "button", "Submit");
 * </pre>
 */
public class Button extends UiElement {

    private Button(Locator locator) {
        super(locator);
    }

    /** Creates a Button from a CSS/XPath selector. */
    public static Button from(Page page, String selector) {
        return new Button(page.locator(selector));
    }

    /** Creates a Button by ARIA role + accessible name. More resilient than ID selectors. */
    public static Button byRole(Page page, String role, String name) {
        return new Button(page.getByRole(
                com.microsoft.playwright.options.AriaRole.valueOf(role.toUpperCase()),
                new Page.GetByRoleOptions().setName(name)));
    }

    /** Creates a Button that matches by visible text content. */
    public static Button byText(Page page, String text) {
        return new Button(page.getByText(text));
    }

    public void click() {
        locator.click();
    }

    public void doubleClick() {
        locator.dblclick();
    }

    /** Returns the visible text content of the button. */
    public String getText() {
        return locator.textContent().trim();
    }
}
