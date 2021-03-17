package com.leroy.utils.file_manager.excel;

import org.apache.poi.ss.usermodel.Row;
import org.testng.util.Strings;

public class ExcelRow {
    private Row row;

    public ExcelRow(Row row) {
        this.row = row;
    }

    public String getCellStringValueByIndex(int index) {
        try {
            return row.getCell(index).getStringCellValue();
        } catch (Exception ignore) {
            return String.valueOf(row.getCell(index).getNumericCellValue());
        }
    }

    public double getCellDoubleValueByIndex(int index) {
        try {
            return row.getCell(index).getNumericCellValue();
        } catch (Exception ignore) {
            String x = row.getCell(index).getStringCellValue();
            return Double.parseDouble(Strings.isNotNullAndNotEmpty(x) ? x : "0.0");
        }
    }

    public ExcelCell getCellByIndex(int index){
        return new ExcelCell(row.getCell(index));
    }
}
