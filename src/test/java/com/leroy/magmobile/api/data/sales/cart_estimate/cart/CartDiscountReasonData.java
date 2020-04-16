package com.leroy.magmobile.api.data.sales.cart_estimate.cart;

import lombok.Data;

@Data
public class CartDiscountReasonData {
    public CartDiscountReasonData() {
    }

    public CartDiscountReasonData(int id) {
        this.reasonId = id;
    }

    private Integer reasonId;
}
