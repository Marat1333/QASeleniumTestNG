package com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate;

import lombok.Data;

import java.util.List;

@Data
public class ProductOrderDataList {

    public ProductOrderDataList(List<ProductOrderData> products) {
        this.products = products;
    }

    List<ProductOrderData> products;

}
