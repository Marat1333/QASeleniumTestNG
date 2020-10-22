package com.leroy.magportal.api.requests.salesdoc;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v4/salesdoc/search")
public class SalesDocSearchV4Get extends CommonSearchRequestBuilder<SalesDocSearchV4Get> {

    public SalesDocSearchV4Get setDocType(String val) {
        if (val == null)
            return this;
        return queryParam("docType", val);
    }

    public SalesDocSearchV4Get setDocId(String val) {
        if (val == null)
            return this;
        return queryParam("docId", val);
    }


}
