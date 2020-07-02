package com.leroy.magmobile.api.data.sales.cart_estimate.estimate;

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
    private List<EstimateCustomerData> customers;
    private List<EstimateProductOrderData> products;
    private String status;
    private Integer documentVersion;
    private Double totalWeight;

    public void increaseDocumentVersion() {
        this.documentVersion++;
    }

}
