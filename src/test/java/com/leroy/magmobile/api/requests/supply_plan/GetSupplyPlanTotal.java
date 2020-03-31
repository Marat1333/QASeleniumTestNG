package com.leroy.magmobile.api.requests.supply_plan;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Method(value = "GET", path = "/supplyPlan/supplyTotal")
public class GetSupplyPlanTotal extends CommonSearchRequestBuilder<GetSupplyPlanTotal> {

    public GetSupplyPlanTotal setDate(LocalDate... dates) {
        StringBuilder datesAsString = new StringBuilder();
        List<LocalDate> datesList = new ArrayList<>();
        Collections.addAll(datesList, dates);
        Iterator<LocalDate> iterator = datesList.iterator();
        for (LocalDate date : datesList) {
            datesAsString.append(date);
            iterator.next();
            if (iterator.hasNext()) {
                datesAsString.append(",");
            }
        }
        return queryParam("dates", datesAsString.toString());
    }
}
