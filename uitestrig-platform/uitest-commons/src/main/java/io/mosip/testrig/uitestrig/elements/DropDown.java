package io.mosip.testrig.uitestrig.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

/**
 * Typed wrapper for dropdown/select elements.
 *
 * Supports both Angular Material {@code mat-select} (overlay pattern) and native
 * HTML {@code <select>} elements. The type is inferred from the resolved element's tag name.
 *
 * <pre>
 *   // Angular Material mat-select
 *   DropDown lang = DropDown.from(page, "#mat-select-0");
 *   lang.selectByText("English");
 *
 *   // Native select
 *   DropDown country = DropDown.from(page, "select#countryCode");
 *   country.selectByText("India");
 * </pre>
 */
public class DropDown extends UiElement {

    private final Page page;

    private DropDown(Page page, Locator locator) {
        super(locator);
        this.page = page;
    }

    /** Creates a DropDown from a CSS/XPath selector. */
    public static DropDown from(Page page, String selector) {
        return new DropDown(page, page.locator(selector));
    }

    /**
     * Selects an option by its visible text.
     *
     * For Angular Material mat-select: clicks the trigger to open the overlay panel,
     * then clicks the matching mat-option.
     * For native select: uses Playwright's selectOption() API.
     */
    public void selectByText(String optionText) {
        String tag = getTagName();
        if ("select".equalsIgnoreCase(tag)) {
            locator.selectOption(new com.microsoft.playwright.options.SelectOption().setLabel(optionText));
        } else {
            locator.click();
            page.locator("mat-option, [role='option']").getByText(optionText).first().click();
        }
    }

    /**
     * Selects an option by its zero-based index within the open dropdown panel.
     * For Angular Material: opens the panel first, then clicks the nth mat-option.
     * For native select: selects by index directly.
     */
    public void selectByIndex(int index) {
        String tag = getTagName();
        if ("select".equalsIgnoreCase(tag)) {
            locator.selectOption(new com.microsoft.playwright.options.SelectOption().setIndex(index));
        } else {
            locator.click();
            page.locator("mat-option, [role='option']").nth(index).click();
        }
    }

    /**
     * Returns the currently selected option text.
     * For mat-select this reads the trigger's text content.
     */
    public String getSelected() {
        return locator.textContent().trim();
    }

    /**
     * Returns all available option texts by opening the dropdown, reading options, then closing.
     * Only applicable to Angular Material dropdowns.
     */
    public List<String> getOptions() {
        locator.click();
        List<String> options = page.locator("mat-option, [role='option']")
                .allTextContents()
                .stream()
                .map(String::trim)
                .toList();
        page.keyboard().press("Escape");
        return options;
    }

    private String getTagName() {
        try {
            Object tag = page.evaluate("el => el.tagName.toLowerCase()", locator.elementHandle());
            return tag != null ? tag.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
