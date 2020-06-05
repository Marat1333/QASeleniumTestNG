package com.leroy.magmobile.api.data.print;

import lombok.Data;

@Data
public class PrintTaskProductData {
    private String lmCode;
    private String barCode;
    private String size;
    private Integer quantity;
    private Double price;
    private String priceUnit;
    private String title;
    private String salesPrice;
    private String futurePriceFromDate;
    private String priceReasonOfChange;
    private Double recommendedPrice;
    private String priceCurrency;
}
