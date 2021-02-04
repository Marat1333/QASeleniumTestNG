package com.leroy.common_mashups.catalogs.data;

import com.leroy.common_mashups.catalogs.data.product.details.PriceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CatalogShopsData extends PriceInfo {
    private String shopId;
    private Double availableStock;
    private Double price;
}
