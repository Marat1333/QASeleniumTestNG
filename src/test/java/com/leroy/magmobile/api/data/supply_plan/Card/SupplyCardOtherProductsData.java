package com.leroy.magmobile.api.data.supply_plan.Card;

import lombok.Data;

import java.util.List;

@Data
public class SupplyCardOtherProductsData {
    private String departmentId;
    private String lmCode;
    private String barCode;
    private Integer orderedQuantity;
    private String title;
    private List<String> images;
}
