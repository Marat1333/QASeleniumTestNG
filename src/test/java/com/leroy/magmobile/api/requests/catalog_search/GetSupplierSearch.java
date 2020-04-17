package com.leroy.magmobile.api.requests.catalog_search;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/suppliers/search")
public class GetSupplierSearch extends CommonSearchRequestBuilder<GetSupplierSearch> {

    public GetSupplierSearch setQuery(String value) {
        return queryParam("query", value);
    }

}
