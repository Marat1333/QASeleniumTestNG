package com.leroy.umbrella_extension.magmobile.data.estimate;

import com.leroy.umbrella_extension.magmobile.data.ProductItemData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class ProductOrderData extends ProductItemData {

    public ProductOrderData() {}
    public ProductOrderData(ProductItemData productItemResponse) {
        this.setLmCode(productItemResponse.getLmCode());
        this.setBarCode(productItemResponse.getBarCode());
        this.setTitle(productItemResponse.getTitle());
        this.setPrice(productItemResponse.getPrice());
        // to be continued if needed
    }

    private final String type = "PRODUCT";
    private Double quantity;
    private String lineId;

}