package com.leroy.core.api;

import com.leroy.constants.EnvConstants;
import com.leroy.core.ContextProvider;
import com.leroy.core.UserSessionData;
import com.leroy.core.asserts.SoftAssertWrapper;
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
    protected String jaegerHost;
    protected String jaegerService;

    @Setter
    private UserSessionData userSessionData;

    protected UserSessionData getUserSessionData() {
        if (userSessionData != null)
            return userSessionData;
        else
            return ContextProvider.getContext().getUserSessionData();
    }

    protected <J> Response<J> execute(RequestBuilder<?> request, final Class<J> type) {
        return execute(request, type, gatewayUrl);
    }

    protected <J> Response<J> execute(RequestBuilder<?> request, final Class<J> type, String url) {
        UserSessionData thisUserSessionData = getUserSessionData();
        request.header("appversion", "autotests");
        if (thisUserSessionData != null && thisUserSessionData.getAccessToken() != null)
            request.bearerAuthHeader(thisUserSessionData.getAccessToken());
        try {
            return executeRequest(request.build(url), type);
        } catch (ProcessingException err) {
            Log.error("Failed execute request: " + request.build(url).toString());
            throw err;
        }
    }

    @Step("Send {request.method} request")
    private <J> Response<J> executeRequest(Request request, Class<J> type) {
        Response<J> response = super.execute(request, type);
        Optional<String> requestId = response.getHeader("x-request-id");
        requestId.ifPresent(s -> Allure.addAttachment("Jaeger Link", "text/uri-list",
                jaegerHost + "/search?service=" + jaegerService +
                        "&tags=%7B\"x-request-id\"%3A\"" + s + "\"%7D"));
        return response;
    }

    @PostConstruct
    protected void init() {
        gatewayUrl = EnvConstants.MAIN_API_HOST;
        jaegerHost = EnvConstants.JAEGER_HOST;
        jaegerService = EnvConstants.JAEGER_SERVICE;
    }

    // ---------------- VERIFICATIONS --------------- //

    public enum ResponseType {
        GET, PUT, POST, DELETE;
    }

    protected SoftAssertWrapper softAssert() {
        return ContextProvider.getContext().getSoftAssert();
    }

    protected void assertThatResponseIsOk(Response<?> response) {
        assertThat(response, successful());
    }

}
