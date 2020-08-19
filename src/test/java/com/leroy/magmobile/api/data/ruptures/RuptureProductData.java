package com.leroy.magmobile.api.data.ruptures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
    private String planningDeliveryTime;
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

    public LocalDateTime getPlanningDeliveryTimeAsLocalDateTime() {
        return LocalDateTime.parse(planningDeliveryTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]'Z'"));
    }

    public void setPlanningDeliveryTimeAsLocalDateTime(LocalDateTime date) {
        this.planningDeliveryTime = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

    @JsonIgnore
    public void generateRandomData() {
        this.setLmCode(RandomStringUtils.randomNumeric(8));
        this.setBarCode(RandomStringUtils.randomNumeric(13));
        this.setImage("https://res.cloudinary.com/lmru/image/upload/LMCode/82036754.jpg"); //hardcode
        this.setTitle(RandomStringUtils.randomAlphanumeric(12));
        this.setGamma(RandomStringUtils.randomAlphabetic(1));
        this.setTop(RandomStringUtils.randomNumeric(1));
        this.setProvider(RandomStringUtils.randomAlphabetic(10));
        this.setPlanningDeliveryTimeAsLocalDateTime(LocalDateTime.now());
        this.setShopStock(0);
        this.setShelfStock(0);
        this.setTheoreticalStock(0);
        this.setPrice(new Random().nextInt(100) + 1);
        this.setLsStock(new Random().nextInt(22) + 1);
        this.setShoppingHallCount(this.getLsStock());
        this.setShelfCount(new Random().nextInt(4)); // available only 0, 1, 2, 3
        this.setStockRmCount(new Random().nextInt(22) + 1);
        this.setTwentyEighty(false);
        this.setRmFeedbackCount(0);
        this.setComment(RandomStringUtils.randomAlphabetic(10));
        this.setActions(Collections.singletonList(ActionData.returnRandomData()));
    }
}
