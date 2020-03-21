package com.leroy.magmobile.api.requests.salesdoc.estimate;

import com.leroy.magmobile.api.requests.salesdoc.cart.BaseCartRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/estimates/sendEmail")
public class EstimateSendEmailRequest extends BaseCartRequest<EstimateSendEmailRequest> {
    public EstimateSendEmailRequest setEstimateId(String val) {
        return queryParam("estimateId", val);
    }
}
