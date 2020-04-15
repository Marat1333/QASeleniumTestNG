package com.leroy.magmobile.api.data.sales.cart_estimate.cart;

import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class CartProductOrderData extends CartEstimateProductOrderData {

    public CartProductOrderData() {
    }

    public CartProductOrderData(ProductItemData productItemData) {
        super(productItemData);
    }

    private Integer stockAdditionBySalesman;
    private CartDiscountData discount;
}
