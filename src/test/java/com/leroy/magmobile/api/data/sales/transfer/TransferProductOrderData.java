package com.leroy.magmobile.api.data.sales.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransferProductOrderData {
    private String lineId;
    @JsonProperty(required = true)
    private String lmCode;
    private String status;
    private String price;
    @JsonProperty(required = true)
    private Integer orderedQuantity;
    private Integer assignedQuantity;
    private String chosenDepartmentId;
}
