package com.leroy.magmobile.api.data.catalog.product;

import lombok.Data;

@Data
public class SalesHistoryData {
    private String yearmonth;
    private String year;
    private String month;
    private Integer quantity;
    private Double amount;
}