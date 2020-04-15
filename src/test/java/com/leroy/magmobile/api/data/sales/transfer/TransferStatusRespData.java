package com.leroy.magmobile.api.data.sales.transfer;

import lombok.Data;

@Data
public class TransferStatusRespData {
    private String status;
    private String message;
    private Integer code;
}
