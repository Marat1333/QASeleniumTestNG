package com.leroy.magmobile.api.data.sales.transfer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TransferSalesDocData {

    @JsonProperty(required = true)
    private String taskId;
    private String status;
    private Integer shopId;
    private String createdBy;
    private String comment;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime createdDate;
    private String pointOfGiveAway;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SXXX")
    private ZonedDateTime dateOfGiveAway;
    private String departmentId;
    private List<TransferProductOrderData> products;

    public TransferSalesDocData addProduct(TransferProductOrderData product) {
        if (products == null)
            products = new ArrayList<>();
        products.add(product);
        return this;
    }

}
