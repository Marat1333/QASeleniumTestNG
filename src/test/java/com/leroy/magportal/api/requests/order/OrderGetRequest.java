package com.leroy.magportal.api.requests.order;

import com.leroy.magmobile.api.requests.order.BaseOrderRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v2/order")
public class OrderGetRequest extends BaseOrderRequest<OrderGetRequest> {

    public static class Extend {
        public static final String PRODUCT_DETAILS = "productDetails";
    }

    public OrderGetRequest setExtend(String value) {
        return queryParam("extend", value);
    }

}
