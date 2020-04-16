package com.leroy.umbrella_extension.authorization;

import com.fasterxml.jackson.databind.JsonNode;
import ru.leroymerlin.qa.core.clients.base.BaseClient;

import javax.annotation.PostConstruct;

public class TestClient extends BaseClient {

    private String gatewayUrl;

    public void doIt() {
        TestReq req = new TestReq();
        execute(req.build(gatewayUrl), JsonNode.class);
    }

    @PostConstruct
    private void init() {
        gatewayUrl = "https://dev-api-internal-op.apigee.lmru.tech";
    }
}

