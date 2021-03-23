package com.leroy.magportal.api.requests.shop;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/stores")
public class GetStoriesRequest extends RequestBuilder<GetStoriesRequest> {

    public GetStoriesRequest setRegionId(Integer value) {
        return queryParam("regionId", value);
    }

    public GetStoriesRequest setByDistanceTo(Integer value) {
        return queryParam("byDistanceTo", value);
    }

    public GetStoriesRequest setProductId(String value) {
        return queryParam("productId", value);
    }

    public GetStoriesRequest setIfModifiedSince(String value) {
        return header("ifModifiedSince", value);
    }

}
