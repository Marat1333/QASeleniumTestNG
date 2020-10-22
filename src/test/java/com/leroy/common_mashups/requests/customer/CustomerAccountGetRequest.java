package com.leroy.common_mashups.requests.customer;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/customerAccount")
public class CustomerAccountGetRequest extends CommonLegoRequest<CustomerAccountGetRequest> {

    public CustomerAccountGetRequest setCustomerNumber(String val) {
        return queryParam("customerNumber", val);
    }

}
