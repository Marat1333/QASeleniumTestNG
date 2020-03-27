package com.leroy.magmobile.api.data.sales.transfer;

import lombok.Data;

import java.util.List;

@Data
public class TransferDataList {
    private List<TransferSalesDocData> items;
    private Integer totalCount;
}
