package com.leroy.umbrella_extension.magmobile.requests.salesdoc.search;

import com.leroy.umbrella_extension.magmobile.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v3/salesdoc/search")
public class GetSalesDocSearchV3 extends CommonSearchRequestBuilder<GetSalesDocSearchV3> {

    public GetSalesDocSearchV3 setDocType(String val) {
        return queryParam("docType", val);
    }

    public GetSalesDocSearchV3 setDocId(String val) {
        return queryParam("docId", val);
    }

}
