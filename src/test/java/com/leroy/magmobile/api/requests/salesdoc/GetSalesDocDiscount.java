package com.leroy.magmobile.api.requests.salesdoc;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/salesdoc/discount")
public class GetSalesDocDiscount extends RequestBuilder<GetSalesDocDiscount> {

    public GetSalesDocDiscount setShopId(String val) {
        return queryParam("shopId", val);
    }

    public GetSalesDocDiscount setLmCode(String val) {
        return queryParam("lmCode", val);
    }

}
