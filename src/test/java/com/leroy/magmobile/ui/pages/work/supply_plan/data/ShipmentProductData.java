package com.leroy.magmobile.ui.pages.work.supply_plan.data;

import lombok.Data;

@Data
public class ShipmentProductData {
    private String lmCode;
    private String barCode;
    private String title;
    private Integer receivedQuantity;
    private Integer plannedQuantity;
}
