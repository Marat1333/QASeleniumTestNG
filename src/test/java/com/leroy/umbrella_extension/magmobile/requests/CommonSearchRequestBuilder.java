package com.leroy.umbrella_extension.magmobile.requests;

import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

public class CommonSearchRequestBuilder<J extends CommonSearchRequestBuilder<J>> extends RequestBuilder<J> {

    public J setPageSize(Integer val) {
        return queryParam("pageSize", val);
    }

    public J setStartFrom(Integer val) {
        return queryParam("startFrom", val);
    }

    public J setShopId(String val) {
        return queryParam("shopId", val);
    }
}
