package com.leroy.common_mashups.catalogs.data.product.reviews;

import lombok.Data;

import java.util.List;

@Data
public class CatalogReviewsOfProductList {
    private List<CatalogReviewsOfProductsData> items;
    private Integer totalCount;
}
