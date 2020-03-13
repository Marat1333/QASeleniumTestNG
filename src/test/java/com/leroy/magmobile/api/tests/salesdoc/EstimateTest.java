package com.leroy.magmobile.api.tests.salesdoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.SessionData;
import com.leroy.magmobile.api.builders.EstimateBuilder;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate.EstimateData;
import com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate.ProductOrderData;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Collections;
import java.util.Random;

public class EstimateTest extends BaseProjectTest {

    @Inject
    private Provider<EstimateBuilder> provider;

    private EstimateBuilder estimateBuilder;

    @Inject
    private AuthClient authClient;

    private EstimateData estimateData;

    @BeforeClass
    private void setUpDefaultSessionData() {
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId("35");
        sessionData.setUserDepartmentId("1");
        sessionData.setAccessToken(authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
                EnvConstants.BASIC_USER_PASS));

        estimateBuilder = provider.get();
        estimateBuilder.setSessionData(sessionData);
    }

    @Test(description = "Create Estimate")
    public void testCreateEstimate() {
        // Prepare request data
        ProductOrderData productOrderData = estimateBuilder.findProducts(1).get(0);
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);

        // Create
        Response<EstimateData> response = estimateBuilder.sendRequestCreate(productOrderData);
        // Check Create
        estimateData = estimateBuilder.assertThatIsCreatedAndGetData(response);
        // Check that created data contains added product
        estimateBuilder.assertThatResponseContainsAddedProducts(response,
                Collections.singletonList(productOrderData));
        productOrderData.setLineId(estimateData.getProducts().get(0).getLineId());
        estimateData.setProducts(Collections.singletonList(productOrderData));
    }

    @Test(description = "Get Estimate")
    public void testGetEstimate() {
        if (estimateData == null)
            throw new IllegalArgumentException("cart data hasn't been created");
        Response<EstimateData> getResp = estimateBuilder.sendRequestGet(estimateData.getEstimateId());
        estimateBuilder.assertThatGetResponseMatches(getResp, estimateData);
    }

    @Test(description = "Update Estimate - change quantity")
    public void testUpdateEstimate() {
        // Prepare request data
        ProductOrderData productOrderData = estimateData.getProducts().get(0);
        productOrderData.setQuantity(productOrderData.getQuantity() + 3);

        // Create
        Response<EstimateData> response = estimateBuilder.sendRequestUpdate(estimateData.getEstimateId(),
                productOrderData);
        // Check update
        estimateData.increaseDocumentVersion();
        estimateBuilder.assertThatGetResponseMatches(response, estimateData);

        // Send get request and check again that the estimate has been updated
        Response<EstimateData> getResp = estimateBuilder.sendRequestGet(estimateData.getEstimateId());
        estimateBuilder.assertThatGetResponseMatches(getResp, estimateData);
    }

    @Test(description = "Delete Estimate")
    public void testDeleteEstimate() {
        Response<JsonNode> response = estimateBuilder.sendRequestDelete(estimateData.getEstimateId(),
                estimateData.getDocumentVersion());
        estimateBuilder.assertThatResponseChangeStatusIsOk(response);

        Response<EstimateData> getResponse = estimateBuilder.sendRequestGet(estimateData.getEstimateId());
        estimateData.setSalesDocStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        estimateData.setStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        estimateData.increaseDocumentVersion();
        estimateBuilder.assertThatGetResponseMatches(getResponse, estimateData);
    }
}
