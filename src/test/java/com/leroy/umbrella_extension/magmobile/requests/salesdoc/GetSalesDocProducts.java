package com.leroy.umbrella_extension.magmobile.requests.salesdoc;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/salesdoc/products")
public class GetSalesDocProducts extends RequestBuilder<GetSalesDocProducts> {

    public GetSalesDocProducts setShopId(String val) {
        return queryParam("shopId", val);
    }

    public GetSalesDocProducts setFullDocId(String val) {
        return queryParam("fullDocId", val);
    }

}
