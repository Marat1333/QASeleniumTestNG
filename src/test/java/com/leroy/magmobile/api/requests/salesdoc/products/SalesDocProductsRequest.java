package com.leroy.magmobile.api.requests.salesdoc.products;

import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

public class SalesDocProductsRequest<T extends SalesDocProductsRequest<T>> extends RequestBuilder<T> {

    public T setFullDocId(String val) {
        return queryParam("fullDocId", val);
    }

    public T setAccessToken(String val) {
        return bearerAuthHeader(val);
    }

    public T setShopId(String val) {
        return queryParam("shopId", val);
    }

    public T setRegionId(String val) {
        return queryParam("regionId", val);
    }

    public T setSalesDocumentData(SalesDocumentResponseData salesDoc) {
        return jsonBody(salesDoc);
    }

}