package com.leroy.core.api;

import com.leroy.constants.EnvConstants;
import com.leroy.core.UserSessionData;
import com.leroy.core.configuration.Log;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.Setter;
import ru.leroymerlin.qa.core.clients.base.BaseClient;
import ru.leroymerlin.qa.core.clients.base.Request;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.commons.annotations.Dependencies;
import ru.leroymerlin.qa.core.commons.enums.Application;

import javax.annotation.PostConstruct;
import javax.ws.rs.ProcessingException;
import java.util.Optional;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

@Dependencies(bricks = Application.MAGMOBILE)
public abstract class BaseMashupClient extends BaseClient {

    protected String gatewayUrl;

    @Setter
    protected UserSessionData userSessionData;

    protected <J> Response<J> execute(RequestBuilder<?> request, final Class<J> type) {
        if (userSessionData != null && userSessionData.getAccessToken() != null)
            request.bearerAuthHeader(userSessionData.getAccessToken());
        try {
            return executeRequest(request.build(gatewayUrl), type);
        } catch (ProcessingException err) {
            Log.error("Failed execute request: " + request.build(gatewayUrl).toString());
            throw err;
        }
    }

    @Step("Send {request.method} request")
    private <J> Response<J> executeRequest(Request request, Class<J> type) {
        Response<J> response = super.execute(request, type);
        Optional<String> requestId = response.getHeader("x-request-id");
        requestId.ifPresent(s -> Allure.addAttachment("Jaeger Link", "text/uri-list",
                EnvConstants.JAEGER_HOST + "/search?service=" + EnvConstants.JAEGER_SERVICE +
                        "&tags=%7B\"x-request-id\"%3A\"" + s + "\"%7D"));
        return response;
    }

    @PostConstruct
    protected void init() {
        gatewayUrl = EnvConstants.MAIN_API_HOST;
    }

    // ---------------- VERIFICATIONS --------------- //

    public enum ResponseType {
        GET, PUT, POST, DELETE;
    }

    protected void assertThatResponseIsOk(Response<?> response) {
        assertThat(response, successful());
    }

}
