package com.leroy.umbrella_extension.magmobile.data.estimate;

import com.leroy.umbrella_extension.magmobile.data.ProductItemResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductOrderData extends ProductItemResponse {

    private String type = "PRODUCT";
    private Double quantity;
    private String lineId;

}