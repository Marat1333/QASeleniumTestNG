package com.leroy.common_mashups.catalogs.requests;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v2/products/{lmCode}/similars")
public class GetCatalogSimilarProductsV2Request extends CommonLegoRequest<GetCatalogSimilarProductsV2Request> {

    public GetCatalogSimilarProductsV2Request setLmCode(String lmCode) {
        return pathParam("lmCode", lmCode);
    }

    public GetCatalogSimilarProductsV2Request setExtend(String value) {
        return queryParam("extend", value);
    }

}
