package com.leroy.magmobile.api.requests.catalog;

import com.leroy.magmobile.api.data.catalog.product.reviews.ReviewData;
import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/catalog/product/review")
public class PostCatalogProductReviewCreate extends CommonLegoRequest<PostCatalogProductReviewCreate> {
    public PostCatalogProductReviewCreate setReviewData(ReviewData data) {
        return this.jsonBody(data);
    }
}
