package com.leroy.magmobile.api.data.sales.transfer;

import lombok.Data;

import java.util.List;

@Data
public class TransferSearchProductDataList {
    List<TransferSearchProductData> items;
    private Integer totalCount;
    private Integer filteredCount;
    private Boolean isLastPage;
}
