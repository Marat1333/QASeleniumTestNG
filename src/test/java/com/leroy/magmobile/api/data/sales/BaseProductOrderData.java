package com.leroy.magmobile.api.data.sales;

import lombok.Data;

@Data
public abstract class BaseProductOrderData {
    private String lineId;
    private String lmCode;
    private String type = "PRODUCT";
    private Double price;
    private Double quantity;
}
