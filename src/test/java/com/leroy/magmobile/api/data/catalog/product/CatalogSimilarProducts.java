package com.leroy.magmobile.api.data.catalog.product;

import lombok.Data;

import java.util.List;

@Data
public class CatalogSimilarProducts {
    private Integer totalCount;
    private List<CatalogProductData> items;
}
