package com.leroy.magportal.api.requests.shop;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "DELETE", path = "/product/{productId}")
public class DeleteStoreProductRequest extends RequestBuilder<DeleteStoreProductRequest> {

    public DeleteStoreProductRequest setProductId(String value) {
        return pathParam("productId", value);
    }
}
