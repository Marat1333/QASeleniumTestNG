package com.leroy.magmobile.api.data.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProductItemDataList {

    @JsonProperty(required = true)
    private List<ProductItemData> items;

}
