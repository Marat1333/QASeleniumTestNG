package com.leroy.umbrella_extension.magmobile.data.estimate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.leroy.umbrella_extension.magmobile.data.customer.CustomerData;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private String documentVersion;
    private Double totalWeight;

}
