package com.leroy.umbrella_extension.magmobile.requests.salesdoc.estimate;

import com.leroy.umbrella_extension.magmobile.requests.salesdoc.cart.BaseCartRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/estimates")
public class EstimateGet extends BaseCartRequest<EstimateGet> {
    public EstimateGet setEstimateId(String val) {
        return queryParam("estimateId", val);
    }
}
