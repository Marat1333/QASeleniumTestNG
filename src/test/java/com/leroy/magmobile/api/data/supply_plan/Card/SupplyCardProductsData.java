package com.leroy.magmobile.api.data.supply_plan.Card;

import lombok.Data;

import java.util.List;

@Data
public class SupplyCardProductsData {
    private String departmentId;
    private String lmCode;
    private String barCode;
    private Integer expectedQuantity;
    private Integer receivedQuantity;
    private Boolean isFullReceived;
    private String title;
    private List<String> images;
}
