package com.leroy.magmobile.api.requests.salesdoc.transfer;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/transfer/products/search")
public class TransferProductSearchRequest extends CommonSearchRequestBuilder<TransferProductSearchRequest> {

    public TransferProductSearchRequest setPointOfGiveAway(String value) {
        return queryParam("pointOfGiveAway", value);
    }

}
