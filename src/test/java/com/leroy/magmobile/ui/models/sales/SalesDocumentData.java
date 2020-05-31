package com.leroy.magmobile.ui.models.sales;

import lombok.Data;

import java.util.List;

@Data
public class SalesDocumentData {
    private String title;
    private String number;
    private String date;
    private String documentState;

    private List<OrderAppData> orderAppDataList;
}
