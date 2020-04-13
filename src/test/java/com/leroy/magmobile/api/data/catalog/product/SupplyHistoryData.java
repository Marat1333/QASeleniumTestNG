package com.leroy.magmobile.api.data.catalog.product;

import lombok.Data;

@Data
public class SupplyHistoryData {
    private String orderNo;
    private String logisticFlow;
    private String plannedDeliveryDate;
    private String actualDeliveryDate;
    private Integer orderedItemQty;
    private Integer receivedItemQty;
    private String supplierDate;
}
