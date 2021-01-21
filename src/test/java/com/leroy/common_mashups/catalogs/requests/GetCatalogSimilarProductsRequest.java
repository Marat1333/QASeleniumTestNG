package com.leroy.common_mashups.catalogs.requests;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/products/{lmCode}/similars")
public class GetCatalogSimilarProductsRequest extends CommonLegoRequest<GetCatalogSimilarProductsRequest> {

    public GetCatalogSimilarProductsRequest setLmCode(String lmCode) {
        return pathParam("lmCode", lmCode);
    }

    public GetCatalogSimilarProductsRequest setExtend(String value) {
        return queryParam("extend", value);
    }

}
