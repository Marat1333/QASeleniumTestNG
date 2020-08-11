package com.leroy.utils.file_manager.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelSheet {
    private Sheet sheet;

    public ExcelSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public Row getRowByIndex(int index) {
        return sheet.getRow(index);
    }

    public String getStringValueFromCell(int rowIndex, int cellIndex) {
        ExcelRow row = new ExcelRow(this.getRowByIndex(rowIndex));
        return row.getCellStringValueByIndex(cellIndex);
    }

    public List<ExcelRow> getRowList(){
        List<ExcelRow> rowList = new ArrayList<>();
        Iterator<Row> iterator = sheet.iterator();
        while(iterator.hasNext()){
            rowList.add(new ExcelRow(iterator.next()));
        }
        return rowList;
    }
}
