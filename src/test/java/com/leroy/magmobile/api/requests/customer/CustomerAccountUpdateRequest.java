package com.leroy.magmobile.api.requests.customer;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "PUT", path = "/customerAccount")
public class CustomerAccountUpdateRequest extends RequestBuilder<CustomerAccountUpdateRequest> {

    public CustomerAccountUpdateRequest setShopId(String val) {
        return queryParam("shopId", val);
    }

    public CustomerAccountUpdateRequest setLdap(String val) {
        return header("ldap", val);
    }

}
