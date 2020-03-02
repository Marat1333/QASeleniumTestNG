package com.leroy.umbrella_extension.magmobile.data.sales;

import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ServiceOrderData;
import lombok.Data;

import java.util.List;

@Data
public class SalesDocumentResponseData {
    private String docId;
    private String fullDocId;
    private String docType;
    private String shopId;
    private String salesDocStatus;
    private String pinCode;
    private String comment;
    private List<ProductOrderData> products;
    private List<ServiceOrderData> services;
    private Double docPriceSum;
    private String newServiceId;
}
