package com.leroy.magmobile.ui.pages.work.supply_plan.data;

import lombok.Data;

@Data

public class ShipmentCardData extends AppointmentCardData {
    private Integer receivedQuantity;
    private Integer expectedQuantity;
    private Boolean isFullReceived;
}
