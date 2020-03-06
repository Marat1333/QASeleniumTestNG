package com.leroy.umbrella_extension.magmobile.data.sales;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaFormat;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaOptions;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaString;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ServiceOrderData;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class SalesDocumentResponseData {

    @JsonProperty(required = true)
    @Pattern(regexp = "\\d+")
    private String docId;

    @JsonProperty(required = true)
    @Pattern(regexp = "\\d+")
    private String fullDocId;

    private String docType;
    private String shopId;

    @JsonProperty(required = true)
    private String salesDocStatus;
    private String pinCode;
    private String comment;
    private List<ProductOrderData> products;
    private List<ServiceOrderData> services;
    private Double docPriceSum;
    private String newServiceId;
}
