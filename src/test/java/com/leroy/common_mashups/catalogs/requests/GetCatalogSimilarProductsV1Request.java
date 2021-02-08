package com.leroy.common_mashups.catalogs.requests;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/products/{lmCode}/similars")
public class GetCatalogSimilarProductsV1Request extends CommonLegoRequest<GetCatalogSimilarProductsV1Request> {

    public GetCatalogSimilarProductsV1Request setLmCode(String lmCode) {
        return pathParam("lmCode", lmCode);
    }

    public GetCatalogSimilarProductsV1Request setExtend(String value) {
        return queryParam("extend", value);
    }

}
