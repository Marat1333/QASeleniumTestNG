package com.leroy.magmobile.api.requests.catalog_search;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/services/search")
public class GetCatalogServicesSearch extends CommonSearchRequestBuilder<GetCatalogServicesSearch> {

    public GetCatalogServicesSearch setLmCode(String val) {
        return queryParam("lmCode", val);
    }

    public GetCatalogServicesSearch setName(String val) {
        return queryParam("name", val);
    }

}