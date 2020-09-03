package com.leroy.magportal.api.data.catalog.products;

import com.leroy.magmobile.api.data.catalog.ProductItemData;
import java.util.List;
import lombok.Data;

@Data
public class CatalogSimilarProductsData {

    List<ProductItemData> substitutes;
    List<ProductItemData> complements;
}
