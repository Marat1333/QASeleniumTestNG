package com.leroy.umbrella_extension.magmobile.requests.catalog_search;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/catalog/services/search")
public class GetCatalogServicesSearch extends RequestBuilder<GetCatalogServicesSearch> {

    public GetCatalogServicesSearch setPageSize(Integer val) {
        return queryParam("pageSize", val);
    }

    public GetCatalogServicesSearch setStartFrom(Integer val) {
        return queryParam("startFrom", val);
    }

    public GetCatalogServicesSearch setLmCode(String val) {
        return queryParam("lmCode", val);
    }

    public GetCatalogServicesSearch setShopId(String val) {
        return queryParam("shopId", val);
    }

    public GetCatalogServicesSearch setDepartmentId(Object val) {
        return queryParam("departmentId", val);
    }

    public GetCatalogServicesSearch setName(String val) {
        return queryParam("name", val);
    }

}