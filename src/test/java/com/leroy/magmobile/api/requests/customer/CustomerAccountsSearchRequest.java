package com.leroy.magmobile.api.requests.customer;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v2/customerAccount/search")
public class CustomerAccountsSearchRequest extends CommonSearchRequestBuilder<CustomerAccountsSearchRequest> {

    public CustomerAccountsSearchRequest setCustomerType(Object value) {
        if (value != null)
            return queryParam("customerType", value);
        else return this;
    }

    public CustomerAccountsSearchRequest setDiscriminantType(Object value) {
        if (value != null)
            return queryParam("discriminantType", value);
        else return this;
    }

    public CustomerAccountsSearchRequest setDiscriminantValue(String value) {
        if (value != null) {
            return queryParam("discriminantValue", value.replaceAll("\\+", "%2B"));
        } else return this;
    }

}
