package com.leroy.magmobile.api.requests.catalog;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/similarProducts")
public class GetCatalogSimilarProducts extends GetCatalogProduct {

    public GetCatalogSimilarProducts setExtend() {
        return (GetCatalogSimilarProducts) queryParam("extend", "rating,logistic,inventory");
    }

    public GetCatalogSimilarProducts setLmCode(String value) {
        return (GetCatalogSimilarProducts) super.setLmCode(value);
    }
}
