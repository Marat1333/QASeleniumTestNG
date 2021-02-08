package com.leroy.common_mashups.catalogs.requests;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/services")
public class GetCatalogServicesRequest extends CommonSearchRequestBuilder<GetCatalogServicesRequest> {

    public GetCatalogServicesRequest setLmCode(String val) {
        return queryParam("lmCode", val);
    }

    public GetCatalogServicesRequest setName(String val) {
        return queryParam("name", val);
    }

}