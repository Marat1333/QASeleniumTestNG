package com.leroy.common_mashups.catalogs.data;

import com.leroy.common_mashups.catalogs.data.product.CatalogProductData;
import lombok.Data;

import java.util.List;

@Data
public class CatalogSimilarProductsDataV2 {

    private Integer totalCount;
    private List<CatalogProductData> items;
}
