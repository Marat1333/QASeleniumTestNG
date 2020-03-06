package com.leroy.umbrella_extension.magmobile.data.sales.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaFormat;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaInject;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaString;
import lombok.Data;

import javax.validation.constraints.Future;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class TransferSalesDocData {

    private String taskId;
    private String status;
    private Integer shopId;
    private String createdBy;
    private String comment;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime createdDate;
    private String pointOfGiveAway;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime dateOfGiveAway;
    private String departmentId;
    private List<TransferProductOrderData> products;

    public TransferSalesDocData addProduct(TransferProductOrderData product) {
        products.add(product);
        return this;
    }

}
