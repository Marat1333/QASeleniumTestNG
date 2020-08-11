package com.leroy.utils.file_manager.excel;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelCell {
    Cell cell;

    public ExcelCell(Cell cell) {
        this.cell = cell;
    }

    public String getCellStringValue() {
        return cell.getStringCellValue();
    }
}
