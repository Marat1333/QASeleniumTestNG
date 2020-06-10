package com.leroy.magportal.api.data;

import lombok.Data;

import java.util.List;

@Data
public class CatalogSimilarProductsData {
    List<ProductData> substitutes;
    List<ProductData> complements;
}
