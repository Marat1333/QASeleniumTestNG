package com.leroy.common_mashups.requests.customer;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/customerAccount")
public class CustomerAccountGetRequest extends RequestBuilder<CustomerAccountGetRequest> {

    public CustomerAccountGetRequest setCustomerNumber(String val) {
        return queryParam("customerNumber", val);
    }

}
