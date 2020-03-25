package com.leroy.magmobile.api.data.sales.transfer;

import lombok.Data;

@Data
public class TransferRunRespData {
    private String taskId;
    private String status;
    private Integer code;
}
