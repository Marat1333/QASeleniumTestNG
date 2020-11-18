package com.leroy.umbrella_extension.tpnet.request;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/api/exchanges/tpnet/brokerExchange/publish")
public class RabbitTpNetPostRequest extends RequestBuilder<RabbitTpNetPostRequest> {

}