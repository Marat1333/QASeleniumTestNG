package com.leroy.umbrella_extension.magmobile.requests.salesdoc.estimate;

import com.leroy.umbrella_extension.magmobile.requests.salesdoc.cart.BaseCartRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/estimates/changeStatus")
public class EstimateChangeStatusPut extends BaseCartRequest<EstimateChangeStatusPut> {

    public EstimateChangeStatusPut setEstimateId(String val) {
        return queryParam("estimateId", val);
    }

}
