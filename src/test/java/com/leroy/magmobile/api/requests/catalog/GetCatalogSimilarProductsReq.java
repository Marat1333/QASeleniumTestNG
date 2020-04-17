package com.leroy.magmobile.api.requests.catalog;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/similarProducts")
public class GetCatalogSimilarProductsReq extends CommonLegoRequest<GetCatalogSimilarProductsReq> {

    public GetCatalogSimilarProductsReq setLmCode(String value) {
        return queryParam("lmCode", value);
    }

    public GetCatalogSimilarProductsReq setExtend(String value) {
        return queryParam("extend", value);
    }
}
