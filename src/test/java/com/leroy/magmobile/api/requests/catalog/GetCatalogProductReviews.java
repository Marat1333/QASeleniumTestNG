package com.leroy.magmobile.api.requests.catalog;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/product/reviews")
public class GetCatalogProductReviews extends CommonSearchRequestBuilder<GetCatalogProductReviews> {

    public GetCatalogProductReviews setLmCode(String val) {
        return queryParam("lmCode", val);
    }

}
