package com.leroy.magmobile.api.clients;

import com.leroy.core.SessionData;
import com.leroy.magmobile.api.data.sales.DiscountData;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.requests.order.OrderWorkflowPut;
import com.leroy.magmobile.api.requests.salesdoc.discount.GetSalesDocDiscount;
import com.leroy.magmobile.api.requests.salesdoc.search.SalesDocSearchV3Get;
import io.qameta.allure.Step;
import lombok.Setter;
import org.json.simple.JSONObject;
import ru.leroymerlin.qa.core.clients.base.BaseClient;
import ru.leroymerlin.qa.core.clients.base.Request;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.commons.annotations.Dependencies;
import ru.leroymerlin.qa.core.commons.enums.Application;

import javax.annotation.PostConstruct;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

@Dependencies(bricks = Application.MAGMOBILE)
public class MagMobileClient extends BaseClient {

    private String gatewayUrl;

    @Setter
    protected SessionData sessionData;

    protected <J> Response<J> execute(RequestBuilder<?> request, final Class<J> type) {
        if (sessionData.getAccessToken() != null)
            request.bearerAuthHeader(sessionData.getAccessToken());
        return executeRequest(request.build(gatewayUrl), type);
    }

    @Step("Send {request.method} request")
    private <J> Response<J> executeRequest(Request request, Class<J> type) {
        return super.execute(request, type);
    }

    @PostConstruct
    private void init() {
        gatewayUrl = params.getProperty("mashuper.magmobile.url");
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

    public Response<DiscountData> getSalesDocDiscount(GetSalesDocDiscount params) {
        return execute(params.build(gatewayUrl), DiscountData.class);
    }

    // ---------------- VERIFICATIONS --------------- //

    protected void assertThatResponseIsOk(Response<?> response) {
        assertThat(response, successful());
    }

}
