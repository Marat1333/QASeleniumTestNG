package com.leroy.magmobile.api.requests.catalog_search;

import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanSuppliers;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET",path = "/suppliers/search")
public class GetSupplierSearch extends GetSupplyPlanSuppliers {

    public GetSupplierSearch setQuery(String value) {
        return (GetSupplierSearch) super.setQuery(value);
    }

    public GetSupplierSearch setPageSize(int pageSize){
        return (GetSupplierSearch) queryParam("pageSize", pageSize);
    }
}
