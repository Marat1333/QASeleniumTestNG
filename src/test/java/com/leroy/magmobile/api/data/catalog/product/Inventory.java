package com.leroy.magmobile.api.data.catalog.product;

import lombok.Data;

import java.util.List;

@Data
public class Inventory {
    private Integer totalQuantity;
    private List<String> source;
}
