package com.leroy.magmobile.api.requests.customer;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/customerAccount")
public class CustomerAccountGetRequest extends RequestBuilder<CustomerAccountGetRequest> {

    public CustomerAccountGetRequest setCustomerNumber(String val) {
        return queryParam("customerNumber", val);
    }

}
