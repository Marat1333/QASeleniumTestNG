package com.leroy.magmobile.api.requests.salesdoc;

import com.leroy.magmobile.api.requests.salesdoc.products.SalesDocProductsRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

import java.util.HashMap;

@Method(value = "PUT", path = "/salesdoc/parameters")
public class SalesDocParametersUpdatePut extends SalesDocProductsRequest<SalesDocParametersUpdatePut> {

    // Query params
    public SalesDocParametersUpdatePut setLdap(String val) {
        return queryParam("ldap", val);
    }

    // Body params

    public SalesDocParametersUpdatePut setPinCode(String val) {
        HashMap<String, String> pinCode = new HashMap<>();
        pinCode.put("pinCode", val);
        return formBody(pinCode);
    }

    public SalesDocParametersUpdatePut setStatus(String val) {
        HashMap<String, String> mapStatus = new HashMap<>();
        mapStatus.put("status", val);
        return formBody(mapStatus);
    }

}
