package com.leroy.magmobile.api.tests.salesdoc;

import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.magmobile.api.clients.SalesDocProductClient;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.ServiceOrderData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;

import java.util.Collections;
import java.util.Random;

import io.qameta.allure.AllureId;
import org.testng.annotations.Test;

public class SalesDocApiTest extends BaseProjectApiTest {

    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private SalesDocProductClient salesDocProductClient;

    private SalesDocumentResponseData salesDocument;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @Test(description = "C3232445 SalesDoc add product")
    @AllureId("13096")
    public void testSalesDocAddProduct() {
        // Prepare request data
        CartEstimateProductOrderData productOrderData = new CartEstimateProductOrderData(
                searchProductHelper.getProducts(1).get(0));
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);

        // Create and check
        salesDocProductClient.sendRequestCreate(productOrderData)
                .assertThatIsCreated(false);

        // update data variable
        salesDocument = salesDocProductClient.getResponseData();
        salesDocument.setProducts(Collections.singletonList(productOrderData));
    }

    @Test(description = "C3232446 SalesDoc add services")
    public void testSalesDocAddService() {
        // Prepare request data
        ServiceOrderData serviceOrderData = new ServiceOrderData(
                searchProductHelper.getServices(1).get(0));
        serviceOrderData.setPrice(10.0);

        // Send requests and verification
        salesDocProductClient.sendRequestCreate(serviceOrderData)
                .assertThatIsCreated(true);

        // Get data
        salesDocument = salesDocProductClient.getResponseData();
        salesDocument.setServices(Collections.singletonList(serviceOrderData));

        salesDocProductClient.sendRequestGet(salesDocument.getFullDocId())
                .assertThatGetResponseMatches(salesDocument);
    }

    @Test(description = "C3232448 SalesDoc product GET")
    @AllureId("13099")
    public void testSalesDocProductGET() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        // Send get
        salesDocProductClient.sendRequestGet(salesDocument.getFullDocId());
        salesDocProductClient.assertThatGetResponseMatches(salesDocument);
    }

    @Test(description = "C22898131 SalesDoc service GET")
    @AllureId("13100")
    public void testSalesDocServiceGET() throws Exception {
        testSalesDocProductGET();
    }

    @Test(description = "C22897826 SalesDoc UPDATE quantity for the same product")
    @AllureId("13102")
    public void testSalesDocUpdateQuantityForTheSameProduct() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        CartEstimateProductOrderData productOrderData = salesDocument.getProducts().get(0);
        productOrderData.setQuantity(productOrderData.getQuantity() + 2);

        salesDocProductClient.updateSalesDocProducts(salesDocument.getFullDocId(), productOrderData)
                .assertThatIsUpdated(salesDocument, false);
        salesDocProductClient.sendRequestGet(salesDocument.getFullDocId());
        salesDocProductClient.assertThatGetResponseMatches(salesDocument);
    }

    @Test(description = "C22898166 SalesDoc UPDATE price for the same service")
    @AllureId("13103")
    public void testSalesDocUpdatePriceForTheSameService() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        ServiceOrderData serviceOrderData = salesDocument.getServices().get(0);
        serviceOrderData.setPrice(serviceOrderData.getPrice() + 101);
        salesDocProductClient.updateSalesDocProducts(salesDocument.getFullDocId(), serviceOrderData)
                .assertThatIsUpdated(salesDocument, true);

        salesDocument.setNewServiceId(salesDocProductClient.getResponseData().getNewServiceId());

        salesDocProductClient.sendRequestGet(salesDocument.getFullDocId())
                .assertThatGetResponseMatches(salesDocument);
    }

    @Test(description = "C22898134 SalesDoc UPDATE parameter - Cancel (For document with Products)")
    @AllureId("13105")
    public void testSalesDocUpdateParameterCancelWithProduct() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        salesDocProductClient.cancelSalesDoc(salesDocument.getFullDocId())
                .assertThatIsCancelled(salesDocument);
    }

    @Test(description = "C22898394 SalesDoc UPDATE parameter - Cancel (For document with Services)")
    @AllureId("13106")
    public void testSalesDocUpdateParameterCancelWithServices() {
        testSalesDocUpdateParameterCancelWithProduct();
    }

}
