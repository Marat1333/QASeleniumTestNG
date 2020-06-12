package com.leroy.magmobile.api.data.catalog.product;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CatalogShopsData extends PriceInfo {
    private Integer shopId;
    private Double availableStock;
}
