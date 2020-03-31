package com.leroy.magmobile.api.requests.supply_plan;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/supplyPlan/suppliers")
public class GetSupplyPlanSuppliers extends CommonSearchRequestBuilder<GetSupplyPlanSuppliers> {
    public GetSupplyPlanSuppliers setQuery(String value){
        return queryParam("query",value);
    }
}
