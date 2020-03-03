package com.leroy.umbrella_extension.common;

import com.fasterxml.jackson.core.type.TypeReference;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.BaseClient;
import ru.leroymerlin.qa.core.clients.base.Request;
import ru.leroymerlin.qa.core.clients.base.Response;

public class LegoBaseClient extends BaseClient {

    @Override
    @Step("Send {request.method} request")
    public <J> Response<J> execute(Request request, TypeReference<J> type) {
        return super.execute(request, type);
    }
}
