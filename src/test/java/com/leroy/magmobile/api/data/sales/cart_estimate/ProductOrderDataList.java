package com.leroy.magmobile.api.data.sales.cart_estimate;

import lombok.Data;

import java.util.List;

@Data
public class ProductOrderDataList {

    public ProductOrderDataList(List<CartEstimateProductOrderData> products) {
        this.products = products;
    }

    List<CartEstimateProductOrderData> products;

}
