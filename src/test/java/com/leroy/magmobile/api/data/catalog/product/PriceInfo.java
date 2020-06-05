package com.leroy.magmobile.api.data.catalog.product;

import lombok.Data;

@Data
public class PriceInfo {
    private String price;
    private String priceUnit;
    private String priceCurrency;
    private Double altPrice;
    private String altPriceUnit;
    private String dateOfChange;
    private String reasonOfChange;
    private Double recommendedPrice;
    private String recommendedUnitSale;
    private String recommendedDateOfChange;
}
