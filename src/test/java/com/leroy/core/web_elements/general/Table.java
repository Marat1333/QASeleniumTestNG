package com.leroy.core.web_elements.general;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.*;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class Table extends BaseWidget {

    @WebFindBy(xpath = ".//tr")
    private ElementList<Element> rows;
    @WebFindBy(xpath = ".//th")
    private ElementList<Element> headers;
    @WebFindBy(xpath = ".//div[@class='grip']")
    private ElementList<Element> resizers;
    protected String XPATH_SPECIFIC_INDEX_TD = "//td[parent::tr][%s]"; // [parent::tr] - workaround for Edge
    private String CSS_TBODY_SPECIFIC_ROW_TD = "tbody>tr:nth-child(%s)>td";
    private String XPATH_TBODY_SPECIFIC_ROW_TD = ".//tbody/tr/td"; // TODO - need to write correct xpath

    public Table(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    // ------- PROTECTED METHODS ------ //

    protected WebElement getCellWebElement(int rowIdx, int colIdx) {
        List<WebElement> tableRows = webElement.findElements(By.tagName("tr"));
        List<WebElement> tableCols = tableRows.get(rowIdx).findElements(By.tagName("td"));
        return tableCols.get(colIdx);
    }

    // ------- PUBLIC METHODS ------- //

    public List<String> getHeaderTextList() throws Exception {
        return headers.getTextList();
    }

    public int getRowCount() {
        try {
            int rowCount = rows.getCount();
            if (rows.getElementList() != null && rows.getElementList().size() != rowCount)
                rows.refresh();
            return rowCount;
        } catch (Exception err) {
            Log.error(err.getMessage());
            return -1;
        }
    }

    /**
     * Get count of columns in the given row
     *
     * @return int
     */
    public int getColumnCount(int rowIdx) {
        initialWebElementIfNeeded();
        try {
            List<WebElement> tableRows = webElement.findElements(By.tagName("tr"));
            return tableRows.get(rowIdx).findElements(By.tagName("td")).size();
        } catch (Exception err) {
            Log.error(err.getMessage());
            return -1;
        }
    }

    public void clickCellByIndex(int rowIdx, int colIdx) {
        clickCellByIndex(rowIdx, colIdx, false);
    }

    public void clickCellByIndex(int rowIdx, int colIdx, boolean isScrollTo) {
        initialWebElementIfNeeded();
        Element cell = new Element(driver, this.getCellWebElement(rowIdx, colIdx));
        if (isScrollTo)
            cell.scrollTo();
        cell.click();
    }

    public List<String> getAllColumnData(int columnIndex) throws Exception {
        ElementList<Element> columnData = new ElementList<>(driver,
                new CustomLocator(By.xpath(getXpath() + String.format(XPATH_SPECIFIC_INDEX_TD, columnIndex + 1))));
        try {
            return columnData.getTextList();
        } catch (StaleElementReferenceException err) {
            return columnData.getTextList();
        }
    }

    public List<String> getAllRowData(int rowIndex) throws Exception {
        initialWebElementIfNeeded();
        ElementList<Element> rowData = new ElementList<>(driver,
                new CustomLocator(By.xpath(String.format(XPATH_TBODY_SPECIFIC_ROW_TD, rowIndex + 1))));
        try {
            return rowData.getTextList();
        } catch (StaleElementReferenceException err) {
            return rowData.getTextList();
        }
    }

    public List<List<String>> getAllData() throws Exception {
        int rowCount = getRowCount();
        ArrayList<List<String>> result = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            List<String> rowData = getAllRowData(i);
            result.add(rowData);
        }
        return result;
    }

    public String getCellText(int rowIdx, int colIdx) {
        initialWebElementIfNeeded();
        return getCellWebElement(rowIdx, colIdx).getText();
    }

    public void rightClickOnCell(int rowIdx, int colIdx) {
        rightClickOnCell(rowIdx, colIdx, false);
    }

    public void rightClickOnCell(int rowIdx, int colIdx, boolean isScrollTo) {
        initialWebElementIfNeeded();
        Element tableCell = new Element(driver, getCellWebElement(rowIdx, colIdx));
        if (isScrollTo)
            tableCell.scrollTo();
        tableCell.rightClick();
    }

    /**
     * Resize column by drag and drop column borders
     *
     * @param columnName
     * @param offset
     * @return columnIndex
     * @throws Exception
     */
    public int resizeColumn(String columnName, int offset) {
        initialWebElementIfNeeded();
        try {
            int columnIndex = getHeaderIndexByColumnName(columnName);

            if (columnIndex > -1) {
                resizers.get(columnIndex).dragAndDrop(offset, 0);
                return columnIndex;
            } else {
                throw new Exception("Column name not defined");
            }
        } catch (Exception e) {
            Log.error(e.toString());
            return -1;
        }

    }

    /**
     * Get header index by column name
     *
     * @param columnName
     * @return Index of column
     * @throws Exception
     */
    private int getHeaderIndexByColumnName(String columnName) throws Exception {

        List<String> nameList = getHeaderTextList();

        for (int i = 0; i < nameList.size(); i++) {
            if (nameList.get(i).trim().equals(columnName)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Get column width
     *
     * @param columnName
     * @return
     * @throws Exception
     */
    public int getColumnWidthByName(String columnName) {
        try {
            int columnIndex = getHeaderIndexByColumnName(columnName);
            return headers.get(columnIndex).getWidth();
        } catch (Exception e) {
            Log.error("Get column width error: " + e.toString());
            return -1;
        }
    }

    public void hoverOverCell(int rowIdx, int colIdx) {
        initialWebElementIfNeeded();
        new Element(driver, getCellWebElement(rowIdx, colIdx)).hoverOver();
    }

    /**
     * Get list of text of rows
     *
     * @return
     */
    private List<String> getRowsTextList() throws Exception {
        rows.refresh();
        return rows.getTextList();
    }

    /**
     * Wait for the table is loaded (Contains data)
     */
    public void waitForTableIsLoaded(int timeout) {
        try {
            waitForVisibility(timeout);
            try {
                WebDriverWait wait = new WebDriverWait(this.driver, timeout);
                wait.until(d -> getRowCount() > 0);
            } catch (TimeoutException e) {
                Log.warn("The table " + getMetaName() + " still doesn't contain data");
            }
        } catch (Exception err) {
            Log.error(err.getMessage());
        }
    }

    public void waitForTableIsLoaded() {
        waitForTableIsLoaded(this.timeout);
    }

    public Element getCellElement(int row, int column) {
        initialWebElementIfNeeded();
        return new Element(driver, getCellWebElement(row, column));
    }

    /**
     * Gets fill color of a given row
     *
     * @param rowIdx
     * @return Color
     * @throws Exception
     */
    public Color getRowFillColor(int rowIdx) throws Exception {
        initialWebElementIfNeeded();
        return Color.fromString(rows.get(rowIdx)
                .getCssValue("background-color"));
    }

}
