package com.leroy.magmobile.api.requests.supply_plan;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Method(value = "GET", path = "/supplyPlan/supplyDetails")
public class GetSupplyPlanDetails extends CommonSearchRequestBuilder<GetSupplyPlanDetails> {

    public final GetSupplyPlanDetails setPagination(Boolean value) {
        return queryParam("pagination", value);
    }

    public GetSupplyPlanDetails setDate(LocalDate... dates) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder datesAsString = new StringBuilder();
        List<LocalDate> datesList = new ArrayList<>();
        Collections.addAll(datesList, dates);
        Iterator<LocalDate> iterator = datesList.iterator();
        for (LocalDate date : dates) {
            datesAsString.append(date);
            iterator.next();
            if (iterator.hasNext()) {
                datesAsString.append(",");
            }
        }
        return queryParam("dates", datesAsString.toString());
    }

    public GetSupplyPlanDetails setDate(List<LocalDate> dates) {
        StringBuilder datesAsString = new StringBuilder();
        Iterator<LocalDate> iterator = dates.iterator();
        for (LocalDate date : dates) {
            datesAsString.append(date);
            iterator.next();
            if (iterator.hasNext()) {
                datesAsString.append(",");
            }
        }
        return queryParam("dates", datesAsString.toString());
    }

    public final GetSupplyPlanDetails setSendingLocations(String value) {
        return queryParam("sendingLocations", value);
    }
}
