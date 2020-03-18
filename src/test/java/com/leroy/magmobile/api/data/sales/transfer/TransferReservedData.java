package com.leroy.magmobile.api.data.sales.transfer;

import lombok.Data;

@Data
public class TransferReservedData {
    private String type;
    private TransferReservedItemData items;
}
