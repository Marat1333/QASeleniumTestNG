package com.leroy.magmobile.api.requests.salesdoc.estimate;

import com.leroy.magmobile.api.requests.salesdoc.cart.BaseCartRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/estimates")
public class EstimatePut extends BaseCartRequest<EstimatePut> {
    public EstimatePut setEstimateId(String val) {
        return queryParam("estimateId", val);
    }
}
