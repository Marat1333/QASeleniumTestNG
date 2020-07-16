package com.leroy.magmobile.ui.pages.work.supply_plan.data;

import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import lombok.Data;

@Data
public class SupplyDailyShipmentInfo {
    String departmentId;
    ShipmentData data;

    public SupplyDailyShipmentInfo(ShipmentData data, String departmentId) {
        this.data = data;
        this.departmentId = departmentId;
    }
}
