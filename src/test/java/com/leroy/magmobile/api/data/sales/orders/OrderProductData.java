package com.leroy.magmobile.api.data.sales.orders;

import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class OrderProductData extends BaseProductOrderData {

    private String title;
    private Integer availableStock;
    private String relationshipConfiguration;
    private String longTail;
    private String stock;
    private String vat;
    private Double confirmedQuantity;
    private String pointOfSale;
    private String pointOfOrigin;

    public OrderProductData() {
    }

    public OrderProductData(ProductItemData productItemResponse) {
        super(productItemResponse);
    }


}
