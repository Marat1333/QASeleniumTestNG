package com.leroy.umbrella_extension.magmobile.data.estimate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductOrderDataList {

    public ProductOrderDataList(List<ProductOrderData> products) {
        this.products = products;
    }

    List<ProductOrderData> products;

}
