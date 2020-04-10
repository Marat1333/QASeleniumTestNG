package com.leroy.magmobile.api.data.ruptures;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Data
public class RuptureProductData {
    private String lmCode;
    private String barCode;
    private String title;
    private String gamma;
    private String top;
    private Integer price;
    private Boolean twentyEighty;
    private String provider;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime planningDeliveryTime;
    private Integer shopStock;
    private Integer shelfStock;
    private Integer theoreticalStock;
    private String image;
    private Integer stockRmCount;
    private Integer lsStock;
    private Integer shoppingHallCount;
    private Integer rmFeedbackCount;
    private List<ActionData> actions;
    private Integer shelfCount;
    private String comment;

    @JsonIgnore
    public void generateRandomData() {
        this.setLmCode(RandomStringUtils.randomNumeric(8));
        this.setBarCode(RandomStringUtils.randomNumeric(13));
        this.setTitle(RandomStringUtils.randomAlphanumeric(12));
        this.setGamma(RandomStringUtils.randomAlphabetic(1));
        this.setTop(RandomStringUtils.randomNumeric(1));
        this.setPrice(new Random().nextInt(100)+1);
        this.setShoppingHallCount(new Random().nextInt(22)+1);
        this.setShelfCount(new Random().nextInt(22)+1);
        this.setStockRmCount(new Random().nextInt(22)+1);
        this.setProvider(RandomStringUtils.randomAlphabetic(10));
        this.setShopStock(new Random().nextInt(22)+1);
        this.setShelfStock(new Random().nextInt(22)+1);
        this.setTheoreticalStock(new Random().nextInt(22)+1);
        this.setLsStock(new Random().nextInt(22)+1);
    }
}
