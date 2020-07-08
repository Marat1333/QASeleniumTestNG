package com.leroy.magmobile.ui.pages.sales.product_card.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SupplyHistoryData {
    private String id;
    private String orderedAmount;
    private String receivedAmount;
    private LocalDate contractDate;
    private LocalDate noteDate;
    private LocalDate receiveDate;
}
