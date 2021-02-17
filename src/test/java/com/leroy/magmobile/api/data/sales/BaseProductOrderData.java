package com.leroy.magmobile.api.data.sales;

import com.leroy.common_mashups.catalogs.data.product.ProductData;
import lombok.Data;

@Data
public class BaseProductOrderData {
    private String lineId;
    private String lmCode;
    private String title;
    private String type = "PRODUCT";
    private Double price;
    private Double quantity;

    public BaseProductOrderData() {
    }

    public BaseProductOrderData(ProductData productDataResponse) {
        this.setLmCode(productDataResponse.getLmCode());
        this.setPrice(productDataResponse.getPrice());
    }
}
