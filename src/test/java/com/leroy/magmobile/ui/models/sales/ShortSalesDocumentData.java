package com.leroy.magmobile.ui.models.sales;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortSalesDocumentData {

    private String title;
    private Double documentTotalPrice;
    private String number;
    private String pin;
    private LocalDateTime date;
    private String documentState;
    private String customerName;

}
