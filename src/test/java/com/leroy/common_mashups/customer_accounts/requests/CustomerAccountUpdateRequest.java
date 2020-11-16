package com.leroy.common_mashups.customer_accounts.requests;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/customerAccount")
public class CustomerAccountUpdateRequest extends CommonLegoRequest<CustomerAccountUpdateRequest> {

    public CustomerAccountUpdateRequest setShopId(String val) {
        return queryParam("shopId", val);
    }

    public CustomerAccountUpdateRequest setLdap(String val) {
        return header("ldap", val)
                .queryParam("ldap", val);
    }

}
