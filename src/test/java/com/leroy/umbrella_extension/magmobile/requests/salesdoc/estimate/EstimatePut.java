package com.leroy.umbrella_extension.magmobile.requests.salesdoc.estimate;

import com.leroy.umbrella_extension.magmobile.requests.salesdoc.cart.BaseCartRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/estimates")
public class EstimatePut extends BaseCartRequest<EstimatePut> {
    public EstimatePut setEstimateId(String val) {
        return queryParam("estimateId", val);
    }
}
