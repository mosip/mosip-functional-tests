package io.mosip.testrig.uitestrig.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

/**
 * Typed wrapper for HTML table and Angular Material mat-table elements.
 *
 * Supports both standard {@code <table>/<tr>/<td>} structure and Angular Material
 * {@code mat-table/mat-row/mat-cell} structure. Pass the appropriate row and cell
 * selectors to the factory methods.
 *
 * <pre>
 *   // Standard HTML table
 *   Table history = Table.from(page, "#historyTable");
 *   int rows = history.getRowCount();
 *   String eid = history.getCell(0, 2);  // row 0, column 2
 *   Locator row = history.findRowByText("SUCCESS");
 *
 *   // Angular Material mat-table
 *   Table matHistory = Table.matTable(page, "#historyTable");
 * </pre>
 */
public class Table extends UiElement {

    private final Page page;
    private final String rowSelector;
    private final String cellSelector;

    private Table(Page page, Locator tableLocator, String rowSelector, String cellSelector) {
        super(tableLocator);
        this.page = page;
        this.rowSelector = rowSelector;
        this.cellSelector = cellSelector;
    }

    /** Creates a Table wrapper for a standard HTML {@code <table>} element. */
    public static Table from(Page page, String tableSelector) {
        return new Table(page, page.locator(tableSelector), "tbody tr", "td");
    }

    /** Creates a Table wrapper for an Angular Material {@code mat-table} element. */
    public static Table matTable(Page page, String tableSelector) {
        return new Table(page, page.locator(tableSelector), "mat-row", "mat-cell");
    }

    /**
     * Creates a Table with custom row and cell selectors.
     * Use when the table structure doesn't match standard or mat-table conventions.
     */
    public static Table custom(Page page, String tableSelector, String rowSelector, String cellSelector) {
        return new Table(page, page.locator(tableSelector), rowSelector, cellSelector);
    }

    /** Returns the number of data rows (excluding header rows). */
    public int getRowCount() {
        return locator.locator(rowSelector).count();
    }

    /**
     * Returns the Locator for a row at the given zero-based index.
     *
     * @param rowIndex zero-based row index
     */
    public Locator getRow(int rowIndex) {
        return locator.locator(rowSelector).nth(rowIndex);
    }

    /**
     * Returns the text content of a cell at the given row and column (both zero-based).
     *
     * @param rowIndex  zero-based row index
     * @param colIndex  zero-based column index
     */
    public String getCell(int rowIndex, int colIndex) {
        return getRow(rowIndex).locator(cellSelector).nth(colIndex).textContent().trim();
    }

    /**
     * Returns all text values in a specific column across all rows.
     *
     * @param colIndex zero-based column index
     */
    public List<String> getColumn(int colIndex) {
        return locator.locator(rowSelector).all().stream()
                .map(row -> row.locator(cellSelector).nth(colIndex).textContent().trim())
                .toList();
    }

    /**
     * Returns the first row Locator that contains the given text in any cell.
     * Returns null if no matching row is found.
     *
     * @param text partial or full text to search for in any cell
     */
    public Locator findRowByText(String text) {
        Locator rows = locator.locator(rowSelector);
        int count = rows.count();
        for (int i = 0; i < count; i++) {
            Locator row = rows.nth(i);
            if (row.textContent().contains(text)) return row;
        }
        return null;
    }

    /**
     * Returns true if any row contains the given text in any cell.
     */
    public boolean containsText(String text) {
        return findRowByText(text) != null;
    }

    /**
     * Returns all text content from the table as a 2D list (rows × cells).
     */
    public List<List<String>> getAllData() {
        return locator.locator(rowSelector).all().stream()
                .map(row -> row.locator(cellSelector).allTextContents()
                        .stream().map(String::trim).toList())
                .toList();
    }
}
