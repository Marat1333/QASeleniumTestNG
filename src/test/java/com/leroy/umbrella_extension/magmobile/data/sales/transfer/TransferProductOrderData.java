package com.leroy.umbrella_extension.magmobile.data.sales.transfer;

import lombok.Data;

@Data
public class TransferProductOrderData {
    private String lineId;
    private String lmCode;
    private String status;
    private String price;
    private String orderedQuantity;
    private String assignedQuantity;
}
