package com.leroy.magmobile.ui.pages.work.supply_plan.data;

import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupplyDailyReserveInfo {
    String departmentId;
    ShipmentData data;

    public SupplyDailyReserveInfo(ShipmentData data, String departmentId){
        this.data = data;
        this.departmentId = departmentId;
    }
}
