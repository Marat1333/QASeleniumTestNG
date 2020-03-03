package com.leroy.umbrella_extension.magmobile.requests.salesdoc;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

import java.util.HashMap;

@Method(value = "PUT", path = "/salesdoc/parameters")
public class PutSalesDocParametersUpdate extends RequestBuilder<PutSalesDocParametersUpdate> {

    // Query params
    public PutSalesDocParametersUpdate setLdap(String val) {
        return queryParam("ldap", val);
    }

    public PutSalesDocParametersUpdate setFullDocId(String val) {
        return queryParam("fullDocId", val);
    }

    public PutSalesDocParametersUpdate setShopId(String val) {
        return queryParam("shopId", val);
    }

    public PutSalesDocParametersUpdate setRegionIdj(String val) {
        return queryParam("regionId", val);
    }

    // Body params

    public PutSalesDocParametersUpdate setPinCode(String val) {
        HashMap<String, String> pinCode = new HashMap<>();
        pinCode.put("pinCode", val);
        return formBody(pinCode);
    }

}
