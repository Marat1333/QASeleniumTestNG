package com.leroy.common_mashups.customer_accounts.requests;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/customerAccount")
public class CustomerAccountGetRequest extends CommonLegoRequest<CustomerAccountGetRequest> {

    public CustomerAccountGetRequest setCustomerNumber(String val) {
        return queryParam("customerNumber", val);
    }

    public CustomerAccountGetRequest setLdap(String value) {
        if (value != null)
            return queryParam("ldap", value);
        else return this;
    }

}
