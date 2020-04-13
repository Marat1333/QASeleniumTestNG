package com.leroy.magmobile.api.data.catalog.product.reviews;

import lombok.Data;

import java.util.List;

@Data
public class CatalogReviewsOfProduct {
    private List<CatalogReviewsOfProductsData>items;
}
