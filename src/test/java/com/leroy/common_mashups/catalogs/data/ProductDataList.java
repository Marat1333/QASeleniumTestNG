package com.leroy.common_mashups.catalogs.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import lombok.Data;

import java.util.List;

@Data
public class ProductDataList {

    @JsonProperty(required = true)
    private List<ProductData> items;

}
