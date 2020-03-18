package com.leroy.magmobile.api.requests.order;

import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

public class BaseOrderRequest<J extends BaseOrderRequest<J>> extends RequestBuilder<J> {

    public J setShopId(String val) {
        return header("shopid", val);
    }

    public J setUserLdap(String val) {
        return header("ldap", val);
    }

}