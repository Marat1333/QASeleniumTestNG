package com.leroy.common_mashups.catalogs.requests;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/suppliers/search")
public class GetCatalogSupplierSearchRequest extends CommonSearchRequestBuilder<GetCatalogSupplierSearchRequest> {

    public GetCatalogSupplierSearchRequest setQuery(String value) {
        return queryParam("query", value);
    }

}
