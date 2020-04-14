package com.leroy.magmobile.api.requests.catalog;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/similarProducts")
public class GetCatalogSimilarProducts extends GetCatalogProduct {

    public GetCatalogSimilarProducts setExtend(String extendOption) {
        return (GetCatalogSimilarProducts) super.setExtend(extendOption);
    }

    public GetCatalogSimilarProducts setLmCode(String value) {
        return (GetCatalogSimilarProducts) super.setLmCode(value);
    }

    public GetCatalogSimilarProducts setShopId(String value){
        return (GetCatalogSimilarProducts) super.setShopId(value);
    }
}
