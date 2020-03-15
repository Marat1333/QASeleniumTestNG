package com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate;

import com.leroy.umbrella_extension.magmobile.data.customer.CustomerData;
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
    private List<ProductOrderData> products;
    private String status;
    private Integer documentVersion;
    private Double totalWeight;

    public void increaseDocumentVersion() {
        this.documentVersion++;
    }

}
