package com.leroy.magmobile.api.data.catalog.product.reviews;

import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.api.data.user.UserData;
import lombok.Data;

@Data
public class ReviewData {
    private String lmCode;
    private Integer rating;
    private Integer priceRating;
    private Integer qualityRating;
    private String timeUsage;
    private String body;
    private Boolean recommended;
    private String pros;
    private String cons;
    private UserData user;
    private ShopData shop;
}
