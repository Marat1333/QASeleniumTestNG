package com.leroy.magmobile.api.data.sales;

import lombok.Data;

@Data
public class BaseProductOrderData {
    private String lineId;
    private String lmCode;
    private String title;
    private String type = "PRODUCT";
    private Double price;
    private Double quantity;
}
