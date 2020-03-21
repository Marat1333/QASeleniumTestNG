package com.leroy.magmobile.api.data.sales.cart_estimate.estimate;

import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import lombok.Data;

import java.util.List;

@Data
public class EstimateData {

    private String estimateId;
    private String fullDocId;
    private String docType;
    private String documentType;
    private String salesDocStatus;
    private String shopId;
    private List<CustomerData> customers;
    private List<CartEstimateProductOrderData> products;
    private String status;
    private Integer documentVersion;
    private Double totalWeight;

    public void increaseDocumentVersion() {
        this.documentVersion++;
    }

}
