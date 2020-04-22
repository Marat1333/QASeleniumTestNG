package com.leroy.magmobile.api.clients;

import com.leroy.constants.EnvConstants;
import com.leroy.core.SessionData;
import com.leroy.magmobile.api.data.sales.SalesDocDiscountData;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.requests.salesdoc.discount.GetSalesDocDiscount;
import com.leroy.magmobile.api.requests.salesdoc.search.SalesDocSearchV3Get;
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
import java.util.Optional;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

@Dependencies(bricks = Application.MAGMOBILE)
public class MagMobileClient extends BaseClient {

    private String gatewayUrl;

    @Setter
    protected SessionData sessionData = new SessionData();

    protected <J> Response<J> execute(RequestBuilder<?> request, final Class<J> type) {
        if (sessionData != null && sessionData.getAccessToken() != null)
            request.bearerAuthHeader(sessionData.getAccessToken());
        return executeRequest(request.build(gatewayUrl), type);
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
    private void init() {
        gatewayUrl = EnvConstants.MAIN_API_HOST;
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
    }

    // ---------  SalesDoc & Orders -------------------- //

    // Lego_salesdoc_search
    public Response<SalesDocumentListResponse> searchForSalesDocumentBy(SalesDocSearchV3Get params) {
        return execute(params
                .build(gatewayUrl), SalesDocumentListResponse.class);
    }

    public Response<SalesDocumentListResponse> getSalesDocumentsByPinCodeOrDocId(String pinCodeOrDocId) {
        return execute(new SalesDocSearchV3Get()
                .queryParam("pinCodeOrDocId", pinCodeOrDocId)
                .build(gatewayUrl), SalesDocumentListResponse.class);
    }

    // Discount

    public Response<SalesDocDiscountData> getSalesDocDiscount(GetSalesDocDiscount params) {
        return execute(params.build(gatewayUrl), SalesDocDiscountData.class);
    }

    // ---------------- VERIFICATIONS --------------- //

    public enum ResponseType {
        GET, PUT, POST, DELETE;
    }

    protected void assertThatResponseIsOk(Response<?> response) {
        assertThat(response, successful());
    }

}
