package com.leroy.umbrella_extension.magmobile.requests.salesdoc;

import com.leroy.umbrella_extension.magmobile.requests.salesdoc.products.SalesDocProductsRequest;
import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

import java.util.HashMap;

@Method(value = "PUT", path = "/salesdoc/parameters")
public class PutSalesDocParametersUpdate extends SalesDocProductsRequest<PutSalesDocParametersUpdate> {

    // Query params
    public PutSalesDocParametersUpdate setLdap(String val) {
        return queryParam("ldap", val);
    }

    // Body params

    public PutSalesDocParametersUpdate setPinCode(String val) {
        HashMap<String, String> pinCode = new HashMap<>();
        pinCode.put("pinCode", val);
        return formBody(pinCode);
    }

    public PutSalesDocParametersUpdate setStatus(String val) {
        HashMap<String, String> mapStatus = new HashMap<>();
        mapStatus.put("status", val);
        return formBody(mapStatus);
    }

}
