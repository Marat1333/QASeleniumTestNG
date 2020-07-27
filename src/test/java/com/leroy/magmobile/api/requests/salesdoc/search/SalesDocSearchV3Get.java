package com.leroy.magmobile.api.requests.salesdoc.search;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v3/salesdoc/search")
public class SalesDocSearchV3Get extends CommonSearchRequestBuilder<SalesDocSearchV3Get> {

    public SalesDocSearchV3Get setDocType(String val) {
        if (val == null)
            return this;
        return queryParam("docType", val);
    }

    public SalesDocSearchV3Get setDocId(String val) {
        if (val == null)
            return this;
        return queryParam("docId", val);
    }


}
