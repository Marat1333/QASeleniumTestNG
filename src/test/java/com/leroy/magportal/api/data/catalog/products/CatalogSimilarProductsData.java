package com.leroy.magportal.api.data.catalog.products;

import com.leroy.magmobile.api.data.catalog.ProductItemData;
import lombok.Data;

import java.util.List;

@Data
public class CatalogSimilarProductsData {
    List<ProductItemData> substitutes;
    List<ProductItemData> complements;
}
