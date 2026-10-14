package io.mosip.testrig.uitestrig.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Typed wrapper for Angular Material date picker ({@code mat-datepicker}).
 *
 * Two interaction strategies:
 * <ol>
 *   <li><b>fill</b> — types a date string directly into the input field. Fast and reliable
 *       when the mat-datepicker-input accepts keyboard input.</li>
 *   <li><b>selectDate</b> — opens the calendar, navigates to the correct month/year using
 *       the Angular Material calendar header buttons, then clicks the target day cell.
 *       Use this when the field is read-only and only accepts picker interaction.</li>
 * </ol>
 *
 * <pre>
 *   DatePicker fromDate = DatePicker.from(page, "#fromPickertext");
 *
 *   // Keyboard entry (fastest)
 *   fromDate.fill("04/25/2025");
 *
 *   // Full calendar navigation
 *   fromDate.selectDate(LocalDate.of(2025, 4, 25));
 * </pre>
 */
public class DatePicker extends UiElement {

    private static final Logger logger = LoggerFactory.getLogger(DatePicker.class);

    private final Page page;

    private DatePicker(Page page, Locator locator) {
        super(locator);
        this.page = page;
    }

    /** Creates a DatePicker bound to an input element (mat-datepicker-input or similar). */
    public static DatePicker from(Page page, String selector) {
        return new DatePicker(page, page.locator(selector));
    }

    /**
     * Types a date string directly into the date input field.
     * The format should match the locale expected by the application (e.g. "MM/DD/YYYY").
     */
    public void fill(String dateString) {
        locator.click();
        locator.fill(dateString);
        locator.press("Tab");
    }

    /**
     * Opens the Angular Material calendar and navigates to the target date.
     *
     * Assumes the standard Angular Material calendar structure:
     *   - Previous month button: {@code button.mat-calendar-previous-button}
     *   - Next month button: {@code button.mat-calendar-next-button}
     *   - Period button (current month/year): {@code button.mat-calendar-period-button}
     *   - Day cells: {@code button.mat-calendar-body-cell[aria-label='Month Day, Year']}
     *
     * @param date the date to select
     */
    public void selectDate(LocalDate date) {
        locator.click();

        Locator calendarPanel = page.locator("mat-calendar").last();
        calendarPanel.waitFor();

        String targetLabel = buildAriaLabel(date);
        Locator targetCell = page.locator("button.mat-calendar-body-cell[aria-label='" + targetLabel + "']");

        if (!targetCell.isVisible()) {
            navigateToMonth(calendarPanel, date);
            targetCell = page.locator("button.mat-calendar-body-cell[aria-label='" + targetLabel + "']");
        }

        targetCell.click();
        logger.debug("DatePicker selected: {}", targetLabel);
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private void navigateToMonth(Locator calendar, LocalDate target) {
        Locator prevBtn = calendar.locator("button.mat-calendar-previous-button");
        Locator nextBtn = calendar.locator("button.mat-calendar-next-button");
        Locator periodBtn = calendar.locator("button.mat-calendar-period-button");

        for (int attempts = 0; attempts < 48; attempts++) {
            String header = periodBtn.textContent().trim();
            LocalDate displayed = parseCalendarHeader(header);
            if (displayed == null) break;

            int displayedYM = displayed.getYear() * 12 + displayed.getMonthValue();
            int targetYM    = target.getYear() * 12 + target.getMonthValue();

            if (displayedYM == targetYM) return;
            if (displayedYM < targetYM) nextBtn.click(); else prevBtn.click();
        }
    }

    private LocalDate parseCalendarHeader(String header) {
        // Header format: "June 2026" or "Jun 2026" depending on locale
        try {
            String[] parts = header.trim().split("\\s+");
            if (parts.length < 2) return null;
            int year = Integer.parseInt(parts[parts.length - 1]);
            String monthStr = parts[0];
            for (Month m : Month.values()) {
                if (m.getDisplayName(TextStyle.FULL, Locale.ENGLISH).equalsIgnoreCase(monthStr)
                 || m.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).equalsIgnoreCase(monthStr)) {
                    return LocalDate.of(year, m, 1);
                }
            }
        } catch (Exception e) {
            logger.debug("Could not parse calendar header '{}': {}", header, e.getMessage());
        }
        return null;
    }

    private String buildAriaLabel(LocalDate date) {
        // Angular Material formats aria-label as "Month Day, Year" e.g. "June 25, 2026"
        String monthName = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return monthName + " " + date.getDayOfMonth() + ", " + date.getYear();
    }
}
