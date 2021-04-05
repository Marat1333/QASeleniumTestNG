package com.leroy.magportal.api.requests.shop;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/product/{productId}")
public class PostStoreProductRequest extends RequestBuilder<PostStoreProductRequest> {

    public PostStoreProductRequest setProductId(String value) {
        return pathParam("productId", value);
    }
}
