package com.leroy.magmobile.ui.models.sales;

import lombok.Data;

@Data
public class ShortSalesDocumentData {

    private String title;
    private Double documentTotalPrice;
    private String number;
    private String pin;
    private String date;
    private String documentState;

}
