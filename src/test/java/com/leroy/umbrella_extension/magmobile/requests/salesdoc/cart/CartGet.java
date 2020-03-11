package com.leroy.umbrella_extension.magmobile.requests.salesdoc.cart;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/cart")
public class CartGet extends BaseCartRequest<CartGet> {

    public CartGet setCartId(String val) {
        return queryParam("cartId", val);
    }

}
