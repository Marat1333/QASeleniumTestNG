package com.leroy.magmobile.api.data.sales;

import com.leroy.magmobile.api.data.catalog.ProductItemData;
import lombok.Data;

@Data
public class BaseProductOrderData {
    private String lineId;
    private String lmCode;
    private String title;
    private String type = "PRODUCT";
    private Double price;
    private Double quantity;

    public BaseProductOrderData() {}
    public BaseProductOrderData(ProductItemData productItemResponse) {
        this.setLmCode(productItemResponse.getLmCode());
        this.setPrice(productItemResponse.getPrice());
    }
}
