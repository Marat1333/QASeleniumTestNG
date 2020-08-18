package com.leroy.utils.file_manager.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

public class ExcelWorkBook {
    private Workbook workbook;

    public ExcelWorkBook(File file) throws IOException {
        this.workbook = WorkbookFactory.create(file);
    }

    public ExcelWorkBook(String fileName) throws IOException {
        this.workbook = WorkbookFactory.create(new File(fileName));
    }

    public ExcelSheet getExcelSheetByIndex(int index) {
        return new ExcelSheet(workbook.getSheetAt(index));
    }

}
