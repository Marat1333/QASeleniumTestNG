package com.leroy.magmobile.api.requests.salesdoc.transfer;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/salesdoc/transfers")
public class TransferSearchRequest extends CommonSearchRequestBuilder<TransferSearchRequest> {

    public TransferSearchRequest setStatus(String val) {
        return queryParam("status", val);
    }

    public TransferSearchRequest setCreatedBy(String val) {
        return queryParam("createdBy", val);
    }

    public TransferSearchRequest setSortField(String val) {
        return queryParam("sortField", val);
    }


}
