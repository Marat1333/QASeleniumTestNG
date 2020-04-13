package com.leroy.magmobile.api.data.supply_plan.Total;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TotalPalletData {

    private LocalDate date;
    private Integer summPalletPlan;
    private Integer summPalletFact;
}
