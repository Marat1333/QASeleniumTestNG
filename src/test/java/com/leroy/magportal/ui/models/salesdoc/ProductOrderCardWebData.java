package com.leroy.magportal.ui.models.salesdoc;

import lombok.Data;

@Data
public class ProductOrderCardWebData {

    private String lmCode;
    private String barCode;
    private String title;
    private Double price;
    private String priceUnit;
    private Double selectedQuantity;
    private Double totalPrice;
    private Double availableTodayQuantity;
    private Double weight;
}
