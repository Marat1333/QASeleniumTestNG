package com.leroy.umbrella_extension.magmobile.requests.salesdoc.products;

import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderDataList;
import com.leroy.umbrella_extension.magmobile.data.estimate.ServiceOrderDataList;
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

    public T setProducts(ProductOrderDataList products) {
        return jsonBody(products);
    }

    public T setServices(ServiceOrderDataList services) {
        return jsonBody(services);
    }

}