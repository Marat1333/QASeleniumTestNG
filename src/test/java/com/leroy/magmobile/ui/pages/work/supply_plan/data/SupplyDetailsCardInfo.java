package com.leroy.magmobile.ui.pages.work.supply_plan.data;

import com.leroy.magmobile.api.data.supply_plan.Card.SupplyCardData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import lombok.Data;

@Data
public class SupplyDetailsCardInfo {
    ShipmentData details;
    SupplyCardData cardInfo;
    String departmentId;

    public SupplyDetailsCardInfo(ShipmentData details, SupplyCardData cardInfo, String departmentId) {
        this.cardInfo = cardInfo;
        this.details = details;
        this.departmentId = departmentId;
    }
}
