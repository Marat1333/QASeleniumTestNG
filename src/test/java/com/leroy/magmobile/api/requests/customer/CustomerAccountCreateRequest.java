package com.leroy.magmobile.api.requests.customer;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/customerAccount")
public class CustomerAccountCreateRequest extends RequestBuilder<CustomerAccountCreateRequest> {

    public CustomerAccountCreateRequest setShopId(String val) {
        return queryParam("shopId", val);
    }

    public CustomerAccountCreateRequest setLdap(String val) {
        return header("ldap", val);
    }

}
