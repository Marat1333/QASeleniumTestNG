package com.leroy.umbrella_extension.magmobile.data.sales.transfer;

import lombok.Data;

@Data
public class TransferReservedData {
    private String type;
    private TransferReservedItemData items;
}
