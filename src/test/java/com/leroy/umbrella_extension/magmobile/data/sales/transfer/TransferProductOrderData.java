package com.leroy.umbrella_extension.magmobile.data.sales.transfer;

import lombok.Data;

@Data
public class TransferProductOrderData {
    private String lineId;
    private String lmCode;
    private String status;
    private String price;
    private Integer orderedQuantity;
    private Integer assignedQuantity;
    private String chosenDepartmentId;
}
