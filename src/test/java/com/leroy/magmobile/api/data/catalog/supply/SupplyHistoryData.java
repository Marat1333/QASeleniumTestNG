package com.leroy.magmobile.api.data.catalog.supply;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupplyHistoryData {
    private String orderNo;
    private String logisticFlow;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]['Z'][XXX]")
    private LocalDateTime plannedDeliveryDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]['Z'][XXX]")
    private LocalDateTime actualDeliveryDate;
    private String orderedItemQty;
    private String receivedItemQty;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]['Z'][XXX]")
    private LocalDateTime supplierDate;
}
