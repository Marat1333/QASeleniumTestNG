package com.leroy.umbrella_extension.magmobile.data.sales;

import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import lombok.Data;

import java.util.List;

@Data
public class SalesDocumentResponseData {
    private String docId;
    private String fullDocId;
    private String salesDocStatus;
    private String pinCode;
    private String comment;
    private List<ProductOrderData> products;
    private Double docPriceSum;
}
