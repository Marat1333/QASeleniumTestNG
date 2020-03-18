package com.leroy.magmobile.api.data.sales.cart_estimate;

import lombok.Data;

import java.util.List;

@Data
public class CartData {
    private String fullDocId;
    private String docType;
    private String salesDocStatus;
    private List<CartEstimateProductOrderData> products;
    private String shopId;
    private String cartId;
    private String documentType;
    private String status;
    private Integer documentVersion;
    private String groupingId;

    public void increaseDocumentVersion() {
        this.documentVersion++;
    }

}
