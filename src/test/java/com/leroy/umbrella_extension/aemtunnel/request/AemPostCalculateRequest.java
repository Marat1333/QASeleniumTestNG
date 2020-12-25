package com.leroy.umbrella_extension.aemtunnel.request;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/checkout/api/v1/home-delivery/{TransactionId}/calculate")
public class AemPostCalculateRequest extends RequestBuilder<AemPostCalculateRequest> {

    public AemPostCalculateRequest setTransactionId(String val) {
        return pathParam("TransactionId", val);
    }
}