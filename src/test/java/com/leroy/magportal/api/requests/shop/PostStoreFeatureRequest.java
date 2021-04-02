package com.leroy.magportal.api.requests.shop;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/product/{productId}/feature")
public class PostStoreFeatureRequest extends RequestBuilder<PostStoreFeatureRequest> {

    public PostStoreFeatureRequest setProductId(String value) {
        return pathParam("productId", value);
    }
}
