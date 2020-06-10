package com.leroy.magportal.api.data.catalog.products.product_fields;

import lombok.Data;

@Data
public class PriceInfo {
    private String price;
    private String priceUnit;
    private String priceCurrency;
    private String dateOfChange;
    private String reasonOfChange;
}
