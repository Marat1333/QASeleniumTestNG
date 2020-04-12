package com.leroy.magmobile.api.requests.supply_plan;

import com.leroy.magmobile.api.requests.CommonLegoRequest;

import java.time.LocalDate;

public class BaseSupplyPlanRequest<J extends BaseSupplyPlanRequest<J>> extends CommonLegoRequest<J> {

    public J setDate(LocalDate... dates) {
        String[] strDates = new String[dates.length];
        for (int i = 0; i < dates.length; i++) {
            strDates[i] = dates[i].toString();
        }
        String result = String.join(",", strDates);
        return queryParam("dates", result);
    }

}
