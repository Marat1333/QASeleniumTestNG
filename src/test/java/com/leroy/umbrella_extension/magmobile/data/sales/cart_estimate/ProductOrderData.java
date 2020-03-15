package com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate;

import com.leroy.umbrella_extension.magmobile.data.catalog.ProductItemData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class ProductOrderData extends ProductItemData {

    public ProductOrderData() {
    }

    public ProductOrderData(ProductItemData productItemResponse) {
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

    private final String type = "PRODUCT";
    private Double quantity;
    private String lineId;

}