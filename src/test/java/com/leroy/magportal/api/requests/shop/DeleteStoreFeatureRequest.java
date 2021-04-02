package com.leroy.magportal.api.requests.shop;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "DELETE", path = "/product/{productId}/feature")
public class DeleteStoreFeatureRequest extends RequestBuilder<DeleteStoreFeatureRequest> {

    public DeleteStoreFeatureRequest setProductId(String value) {
        return pathParam("productId", value);
    }

    public DeleteStoreFeatureRequest setFeaturesId (String value) {
        return queryParam("featuresId", value);
    }
}
