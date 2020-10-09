package com.leroy.magmobile.api.requests.salesdoc.cart;

import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

public class BaseCartRequest<J extends BaseCartRequest<J>> extends RequestBuilder<J> {

    public J setShopId(String val) {
        return header("shopid", val);
    }

    public J setLdap(String val) { return header("ldap", val); }

    public J setCartId(String val) {
        return queryParam("cartId", val);
    }

}
