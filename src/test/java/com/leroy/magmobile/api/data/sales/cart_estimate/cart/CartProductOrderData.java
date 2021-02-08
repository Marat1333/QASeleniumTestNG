package com.leroy.magmobile.api.data.sales.cart_estimate.cart;

import com.leroy.common_mashups.catalogs.data.product.ProductData;
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

    public CartProductOrderData(ProductData productData) {
        super(productData);
    }

    private Double stockAdditionBySalesman;
    private CartDiscountData discount;
}
