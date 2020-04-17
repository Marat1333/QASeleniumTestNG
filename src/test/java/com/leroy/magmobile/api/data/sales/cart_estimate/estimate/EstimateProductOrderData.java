package com.leroy.magmobile.api.data.sales.cart_estimate.estimate;

import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class EstimateProductOrderData extends CartEstimateProductOrderData {

    public EstimateProductOrderData() {
    }

    public EstimateProductOrderData(ProductItemData productItemData) {
        super(productItemData);
    }


}