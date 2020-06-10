package com.leroy.magportal.api.data.products;

import lombok.Data;

import java.util.List;

@Data
public class Inventory {
    private Integer totalQuantity;
    private List<String> source;
}
