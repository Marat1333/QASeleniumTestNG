package com.leroy.magmobile.api.data.supply_plan.Card;

import lombok.Data;

import java.util.List;

@Data
public class SupplyCardShipmentsData {
    private String shipmentId;
    private String secRecDate;
    private Integer palletPlanQuantity;
    private Integer palletFactQuantity;
    private String status;
    private Boolean isFullReceived;
    private String logFlow;
    private List<SupplyCardProductsData> products;
}
