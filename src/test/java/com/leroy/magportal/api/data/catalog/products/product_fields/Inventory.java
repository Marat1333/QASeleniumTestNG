package com.leroy.magportal.api.data.catalog.products.product_fields;

import lombok.Data;

import java.util.List;

@Data
public class Inventory {
    private Integer totalQuantity;
    private List<String> source;
}
