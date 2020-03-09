package com.leroy.umbrella_extension.magmobile.requests.salesdoc.cart;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/cart")
public class CartPOST extends RequestBuilder<CartPOST> {

    public CartPOST setShopId(String val) {
        return header("shopid", val);
    }

}
