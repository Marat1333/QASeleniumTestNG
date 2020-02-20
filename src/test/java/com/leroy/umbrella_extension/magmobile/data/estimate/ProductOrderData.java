package com.leroy.umbrella_extension.magmobile.data.estimate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductOrderData {

    private String type = "PRODUCT";
    private Double quantity;
    private Double price;
    private String lmCode;
    private String lineId;
    private Double availableStock;

}