package com.leroy.magmobile.api.requests.salesdoc.estimate;

import com.leroy.magmobile.api.requests.salesdoc.cart.BaseCartRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/estimates")
public class EstimateGet extends BaseCartRequest<EstimateGet> {
    public EstimateGet setEstimateId(String val) {
        return queryParam("estimateId", val);
    }
}
