package com.leroy.magmobile.api.requests.supply_plan;

import ru.leroymerlin.qa.core.clients.base.Method;

import java.time.LocalDate;
import java.util.List;

@Method(value = "GET", path = "/supplyPlan/supplyDetails")
public class GetSupplyPlanDetails extends BaseSupplyPlanRequest<GetSupplyPlanDetails> {

    public final GetSupplyPlanDetails setPagination(Boolean value) {
        return queryParam("pagination", value);
    }

    public GetSupplyPlanDetails setDate(List<LocalDate> dates) {
        String[] strDates = new String[dates.size()];
        for (int i = 0; i < dates.size(); i++) {
            strDates[i] = dates.get(i).toString();
        }
        String result = String.join(",", strDates);
        return queryParam("dates", result);
    }

    public final GetSupplyPlanDetails setSendingLocations(String value) {
        return queryParam("sendingLocations", value);
    }
}
