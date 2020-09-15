package com.leroy.magportal.api.requests.order;

import com.leroy.magmobile.api.requests.order.BaseOrderRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/orders/toGiveAway")
public class OrderFulfilmentGivenAwayRequest extends
        BaseOrderRequest<OrderFulfilmentGivenAwayRequest> {

    public OrderFulfilmentGivenAwayRequest setFulfillmentTaskId(String val) {
        return queryParam("fulfillmentTaskId", val);
    }
}
