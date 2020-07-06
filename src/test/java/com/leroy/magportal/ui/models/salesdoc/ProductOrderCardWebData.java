package com.leroy.magportal.ui.models.salesdoc;

import com.leroy.utils.ParserUtil;
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

    // Discounts
    private Double totalPriceWithDiscount;
    private Double discountPercent;

    public void setDiscountPercent(Double discountPercent) {
        setDiscountPercent(discountPercent, false);
    }

    public void setDiscountPercent(Double discountPercent, boolean reCalculate) {
        this.discountPercent = discountPercent;
        if (reCalculate) {
            this.totalPriceWithDiscount = ParserUtil.minus(totalPrice, (totalPrice * discountPercent / 100), 2);
        }
    }
}
