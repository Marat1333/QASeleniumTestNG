package com.leroy.common_mashups.catalogs.data.product.details;

import lombok.Data;

@Data
public class PriceInfo {
    private Double price;
    private Double altPrice;
    private String altPriceUnit;
    private String priceUnit;
    private String priceCurrency;
    private String dateOfChange;
    private String reasonOfChange;
    private Double recommendedPrice;
    private String recommendedUnitSale;
    private String recommendedDateOfChange;
}
