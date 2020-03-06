package com.leroy.umbrella_extension.magmobile.data.estimate;

import com.leroy.umbrella_extension.magmobile.data.ProductItemData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductOrderData extends ProductItemData {

    private String type = "PRODUCT";
    private Double quantity;
    private String lineId;

}