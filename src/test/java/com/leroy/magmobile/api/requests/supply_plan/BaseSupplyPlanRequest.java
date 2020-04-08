package com.leroy.magmobile.api.requests.supply_plan;

import com.leroy.magmobile.api.requests.CommonLegoRequest;

import java.time.LocalDate;

public class BaseSupplyPlanRequest<J extends BaseSupplyPlanRequest> extends CommonLegoRequest<BaseSupplyPlanRequest<J>> {
    public J setDate(LocalDate... dates) {

        String[] strDates = new String[dates.length];
        for (int i=0;i<dates.length;i++) {
            strDates[i]=dates[i].toString();
        }
        String result = String.join(",", strDates);
        return (J) queryParam("dates", result);
    }

    @Override
    public J setLdap(String val) {
        return (J) super.setLdap(val);
    }

    @Override
    public J setShopId(String val) {
        return (J) super.setShopId(val);
    }

    @Override
    public J setDepartmentId(Object val) {
        return (J) super.setDepartmentId(val);
    }
}
