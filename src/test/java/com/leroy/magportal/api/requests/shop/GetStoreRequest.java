package com.leroy.magportal.api.requests.shop;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/stores/{id}")
public class GetStoreRequest extends RequestBuilder<GetStoreRequest> {

    public GetStoreRequest setId(Integer value) {
        return pathParam("id", value);
    }
}
