package com.leroy.umbrella_extension.lsaddress;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.umbrella_extension.lsaddress.requests.DeleteLsAddressAlleyRequest;
import ru.leroymerlin.qa.core.clients.base.BaseClient;
import ru.leroymerlin.qa.core.clients.base.Response;

import javax.annotation.PostConstruct;

public class LsAddressBackClient extends BaseClient {

    private String gatewayUrl;
    private String apiKey = "g9yens4v2C4w5D6J6zvQABYbvN41yxBC";

    public Response<JsonNode> deleteAlley(int alleyId) {
        DeleteLsAddressAlleyRequest req = new DeleteLsAddressAlleyRequest();
        req.setAlleyId(alleyId);
        req.apiKey(apiKey);
        return execute(req.build(gatewayUrl), JsonNode.class);
    }

    @PostConstruct
    private void init() {
        gatewayUrl = "https://dev-api-internal-op.apigee.lmru.tech";
    }
}

