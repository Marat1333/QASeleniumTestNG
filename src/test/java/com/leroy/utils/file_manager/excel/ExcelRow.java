package com.leroy.utils.file_manager.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelRow {
    private Row row;

    public ExcelRow(Row row) {
        this.row = row;
    }

    public String getCellStringValueByIndex(int index) {
        return row.getCell(index).getStringCellValue();
    }

    public double getCellDoubleValueByIndex(int index) {
        return row.getCell(index).getNumericCellValue();
    }

    public Cell getCellByIndex(int index){
        return row.getCell(index);
    }
}
