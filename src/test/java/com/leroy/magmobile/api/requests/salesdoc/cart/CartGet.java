package com.leroy.magmobile.api.requests.salesdoc.cart;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/cart")
public class CartGet extends BaseCartRequest<CartGet> {

    public CartGet setCartId(String val) {
        return queryParam("cartId", val);
    }

}
