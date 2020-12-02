package com.leroy.magportal.api.requests.order;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/additionalProductsInfo")
public class OrderGetAdditionalProductsInfo extends
        CommonLegoRequest<OrderGetAdditionalProductsInfo> {

    public OrderGetAdditionalProductsInfo setLmCodes(String val) {
        return queryParam("lmCodes", val);
    }

    public OrderGetAdditionalProductsInfo setShopId(String val) {
        return queryParam("shopId", val);
    }
}