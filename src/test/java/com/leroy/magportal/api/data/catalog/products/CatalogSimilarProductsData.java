package com.leroy.magportal.api.data.catalog.products;

import lombok.Data;

import java.util.List;

@Data
public class CatalogSimilarProductsData {
    List<ProductData> substitutes;
    List<ProductData> complements;
}
