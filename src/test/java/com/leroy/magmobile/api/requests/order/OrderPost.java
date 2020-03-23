package com.leroy.magmobile.api.requests.order;


import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/orders")
public class OrderPost extends BaseOrderRequest<OrderPost> {

    public OrderPost setCartId(String val) {
        return queryParam("cartId", val);
    }

}
