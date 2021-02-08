package com.leroy.common_mashups.catalogs.data.product.details;

import java.util.List;
import lombok.Data;

@Data
public class Inventory {

    private Integer totalQuantity;
    private List<String> source;
}
