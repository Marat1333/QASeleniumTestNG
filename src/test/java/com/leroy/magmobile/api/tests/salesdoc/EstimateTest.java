package com.leroy.magmobile.api.tests.salesdoc;

import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.clients.EstimateClient;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.SendEmailData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;

import java.util.Collections;
import java.util.Random;

import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class EstimateTest extends BaseProjectApiTest {

    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private EstimateClient estimateClient;

    private EstimateData estimateData;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @Test(description = "C3311701 POST estimate product - HP")
    @AllureId("12920")
    public void testCreateEstimate() {
        // Prepare request data
        EstimateProductOrderData productOrderData = new EstimateProductOrderData(
                searchProductHelper.getProducts(1).get(0));
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);

        // Create
        Response<EstimateData> response = estimateClient.sendRequestCreate(productOrderData);
        // Check Create
        estimateData = estimateClient.assertThatIsCreatedAndGetData(response);
        // Check that created data contains added product
        estimateClient.assertThatResponseContainsAddedProducts(response,
                Collections.singletonList(productOrderData));
        productOrderData.setLineId(estimateData.getProducts().get(0).getLineId());
        estimateData.setProducts(Collections.singletonList(productOrderData));
    }

    @Test(description = "C23194973 Estimate - Send Email")
    @AllureId("12933")
    public void testEstimateSendEmail() {
        if (estimateData == null)
            throw new IllegalArgumentException("estimate data hasn't been created");
        SendEmailData emailData = new SendEmailData();
        emailData.setShopName("TestShopName");
        emailData.setShopAddress("TestShopAddress");
        emailData.setEmails(Collections.singletonList("someEmail@mail.com")); // TODO #unfinished
        Response<JsonNode> resp = estimateClient.sendEmail(estimateData.getEstimateId(), emailData);
        assertThat(resp.toString(), resp.isSuccessful());
    }

    @Test(description = "C3311703 GET estimate product")
    @AllureId("12921")
    public void testGetEstimate() {
        if (estimateData == null)
            throw new IllegalArgumentException("estimate data hasn't been created");
        Response<EstimateData> getResp = estimateClient.sendRequestGet(estimateData.getEstimateId());
        estimateClient.assertThatGetResponseMatches(getResp, estimateData);
    }

    @Test(description = "C3311707 Update Estimate - change quantity")
    @AllureId("12923")
    public void testUpdateEstimate() {
        if (estimateData == null)
            throw new IllegalArgumentException("estimate data hasn't been created");
        // Prepare request data
        EstimateProductOrderData productOrderData = estimateData.getProducts().get(0);
        productOrderData.setQuantity(productOrderData.getQuantity() + 3);

        // Create
        Response<EstimateData> response = estimateClient.sendRequestUpdate(estimateData.getEstimateId(),
                productOrderData);
        // Check update
        estimateData.increaseDocumentVersion();
        estimateClient.assertThatGetResponseMatches(response, estimateData, BaseMashupClient.ResponseType.PUT);

        // Send get request and check again that the estimate has been updated
        Response<EstimateData> getResp = estimateClient.sendRequestGet(estimateData.getEstimateId());
        estimateClient.assertThatGetResponseMatches(getResp, estimateData);
    }

    @Test(description = "C22732369 PUT estimates/changeStatus DRAFT -> DELETED")
    @AllureId("12931")
    public void testDeleteEstimate() {
        if (estimateData == null)
            throw new IllegalArgumentException("estimate data hasn't been created");
        Response<JsonNode> response = estimateClient.sendRequestDelete(estimateData.getEstimateId());
        estimateClient.assertThatResponseChangeStatusIsOk(response);

        Response<EstimateData> getResponse = estimateClient.sendRequestGet(estimateData.getEstimateId());
        estimateData.setSalesDocStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        estimateData.setStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        estimateData.increaseDocumentVersion();
        estimateClient.assertThatGetResponseMatches(getResponse, estimateData);
    }
}
