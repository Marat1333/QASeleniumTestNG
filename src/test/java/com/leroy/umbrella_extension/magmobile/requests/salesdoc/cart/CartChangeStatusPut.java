package com.leroy.umbrella_extension.magmobile.requests.salesdoc.cart;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "PUT", path = "/cart/changeStatus")
public class CartChangeStatusPut extends RequestBuilder<CartGet> {

    public CartGet setCartId(String val) {
        return queryParam("cartId", val);
    }

}
