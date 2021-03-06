package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/ruptures/sessions/{sessionId}/products")
public class RupturesSessionProductsRequest extends CommonSearchRequestBuilder<RupturesSessionProductsRequest> {

    public RupturesSessionProductsRequest setSessionId(Object val) {
        return pathParam("sessionId", val);
    }

    public RupturesSessionProductsRequest setActionState(boolean val) {
        return queryParam("actionState", val);
    }

    public RupturesSessionProductsRequest setAction(int val) {
        return queryParam("action", val);
    }

    public RupturesSessionProductsRequest setProductState(Integer[] val) {
        return queryParam("productState", StringUtils.join(ArrayUtils.toArray(val), ","));
    }
}
