package com.leroy.common_mashups.catalogs.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CatalogComplementaryProductsDataV2 extends CatalogSimilarProductsDataV2 {
    private String parentLmCode;
}
