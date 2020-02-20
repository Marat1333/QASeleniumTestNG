package com.leroy.umbrella_extension.magmobile.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartData {
    private String fullDocId;
    private String docType;
    private String salesDocStatus;
    private List<ProductOrderData> products;
    private String shopId;
    private String cartId;
    private String documentType;
    private String status;
    private Integer documentVersion;
    private String groupingId;

}
