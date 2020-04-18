package com.leroy.magmobile.api.data.sales.transfer;

import lombok.Data;

@Data
public class TransferSearchProductData {
    private String lmCode;
    private Integer totalQuantity;
    private Object source;
    private String altPrice;
    private String priceUnit;
    private String altPriceUnit;
    private Integer recommendedPrice;
    private String priceCurrency;
    private Integer price;
}
