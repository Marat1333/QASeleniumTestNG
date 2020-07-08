package com.leroy.magmobile.ui.models.product_card;

import lombok.Data;

@Data
public class ReviewCardData {
    private String name;
    private String date;
    private String city;
    private String reviewBody;
    private String advantages;
    private String disadvantages;
    private boolean recommendedProduct;
}
