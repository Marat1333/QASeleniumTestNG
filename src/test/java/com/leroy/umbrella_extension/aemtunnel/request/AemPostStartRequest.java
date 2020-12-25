package com.leroy.umbrella_extension.aemtunnel.request;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/checkout/api/v1")
public class AemPostStartRequest extends RequestBuilder<AemPostStartRequest> {

    public AemPostStartRequest setLead(String val) {
        return header("lead", val);
    }

    public AemPostStartRequest setChannel(String val) {
        return header("channel", val);
    }

    public AemPostStartRequest setCustomerCoordinates(String val) {
        return header("Customer-Coordinates", val);
    }
}