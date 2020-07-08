package com.leroy.magmobile.ui.pages.work.supply_plan.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipmentCardData {
    private String name;
    private LocalDateTime dateAndTime;
    private Integer receivedQuantity;
    private Integer expectedQuantity;
    private Boolean isFullReceived;
}
