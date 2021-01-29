package com.leroy.common_mashups.catalogs.data;

import com.leroy.common_mashups.catalogs.data.product.ProductData;
import java.util.List;
import lombok.Data;

@Data
public class CatalogSimilarProductsDataV2 {

    private Integer totalCount;
    private List<ProductData> items;
}
