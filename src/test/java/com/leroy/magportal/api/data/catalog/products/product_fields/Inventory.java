package com.leroy.magportal.api.data.catalog.products.product_fields;

import java.util.List;
import lombok.Data;

@Data
public class Inventory {

    private Integer totalQuantity;
    private List<String> source;
}
