package com.leroy.magmobile.api.data.sales.cart_estimate;

import com.leroy.common_mashups.catalogs.data.product.ProductData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class CartEstimateProductOrderData extends ProductData {

    public CartEstimateProductOrderData() {
    }

    public CartEstimateProductOrderData(ProductData productItemResponse) {
        this.setLmCode(productItemResponse.getLmCode());
        this.setBarCode(productItemResponse.getBarCode());
        this.setTitle(productItemResponse.getTitle());
        this.setPrice(productItemResponse.getPrice());
        this.setAvailableStock(productItemResponse.getAvailableStock());
        this.setPriceUnit(productItemResponse.getPriceUnit());
        this.setDepartmentId(productItemResponse.getDepartmentId());
        this.setTopEM(productItemResponse.getTopEM());
        // to be continued if needed
    }

    private String type = "PRODUCT";
    private Double quantity;
    private String lineId;

}