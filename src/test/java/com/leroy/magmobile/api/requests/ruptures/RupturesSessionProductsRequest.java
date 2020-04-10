package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/ruptures/session/products")
public class RupturesSessionProductsRequest extends CommonLegoRequest<RupturesSessionProductsRequest> {

    public RupturesSessionProductsRequest setSessionId(int val) {
        return queryParam("sessionId", val);
    }
}
