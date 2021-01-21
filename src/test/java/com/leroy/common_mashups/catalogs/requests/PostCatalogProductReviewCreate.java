package com.leroy.common_mashups.catalogs.requests;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/v1/products/{lmCode}/review")
public class PostCatalogProductReviewCreate extends
        CommonLegoRequest<PostCatalogProductReviewCreate> {

    public PostCatalogProductReviewCreate setLmCode(String val) {
        return pathParam("lmCode", val);
    }
}
