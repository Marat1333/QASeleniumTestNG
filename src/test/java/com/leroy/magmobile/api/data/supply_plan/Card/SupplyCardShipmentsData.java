package com.leroy.magmobile.api.data.supply_plan.Card;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SupplyCardShipmentsData {
    private String shipmentId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]['Z']")
    private LocalDateTime secRecDate;
    private Integer palletPlanQuantity;
    private Integer palletFactQuantity;
    private String status;
    private Boolean isFullReceived;
    private String logFlow;
    private List<SupplyCardProductsData> products;
}
