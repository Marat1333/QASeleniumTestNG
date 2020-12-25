package com.leroy.umbrella_extension.aemtunnel.request;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/checkout/api/v1/home-delivery/{TransactionId}")
public class AemGetDeliveryRequest extends RequestBuilder<AemGetDeliveryRequest> {

    public AemGetDeliveryRequest setTransactionId(String val) {
        return pathParam("TransactionId", val);
    }
}