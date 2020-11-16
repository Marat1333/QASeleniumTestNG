package com.leroy.common_mashups.customer_accounts.requests;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/customerAccount/balance")
public class CustomerAccountBalanceRequest extends RequestBuilder<CustomerAccountBalanceRequest> {

    public CustomerAccountBalanceRequest setCustomerNumber(String val) {
        return queryParam("customerNumber", val);
    }

}
