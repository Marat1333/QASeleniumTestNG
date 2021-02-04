package com.leroy.common_mashups.catalogs.requests;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/products/{lmCode}/reviews")
public class GetCatalogProductReviewsRequest extends CommonSearchRequestBuilder<GetCatalogProductReviewsRequest> {

    public GetCatalogProductReviewsRequest setLmCode(String val) {
        return pathParam("lmCode", val);
    }

}
