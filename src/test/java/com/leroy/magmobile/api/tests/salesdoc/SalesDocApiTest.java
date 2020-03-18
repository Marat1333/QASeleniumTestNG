package com.leroy.magmobile.api.tests.salesdoc;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.SessionData;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.SalesDocProductClient;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.data.sales.cart_estimate.ProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.ServiceOrderData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Random;

public class SalesDocApiTest extends BaseProjectApiTest {

    @Inject
    private SalesDocProductClient salesDocProductClient;

    @Inject
    private CatalogSearchClient searchClient;

    @Inject
    private AuthClient authClient;

    private SalesDocumentResponseData salesDocument;

    @BeforeClass
    private void setUpDefaultSessionData() {
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId("35");
        sessionData.setUserDepartmentId("1");
        sessionData.setAccessToken(authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
                EnvConstants.BASIC_USER_PASS));

        salesDocProductClient.setSessionData(sessionData);
        searchClient.setSessionData(sessionData);
    }

    @Test(description = "C3232445 SalesDoc add product")
    public void testSalesDocAddProduct() {
        // Prepare request data
        ProductOrderData productOrderData = new ProductOrderData(searchClient.getProducts(1).get(0));
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);

        // Create and check
        salesDocProductClient.sendRequestCreate(productOrderData)
                .assertThatIsCreated(false);

        // Get data
        salesDocument = salesDocProductClient.getResponseData();
        salesDocument.setProducts(Collections.singletonList(productOrderData));

        // Send get
        salesDocProductClient.sendRequestGet(salesDocument.getFullDocId());
        salesDocProductClient.assertThatGetResponseMatches(salesDocument);
    }

    @Test(description = "C3232446 SalesDoc add services")
    public void testSalesDocAddService() {
        // Prepare request data
        ServiceOrderData serviceOrderData = new ServiceOrderData(searchClient.getServices(1).get(0));
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
    public void testSalesDocProductGET() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        salesDocProductClient.sendRequestGet(salesDocument.getFullDocId())
                .assertThatResponseIsValid();
    }

    @Test(description = "C22898131 SalesDoc service GET")
    public void testSalesDocServiceGET() throws Exception {
        testSalesDocProductGET();
    }

    @Test(description = "C22897826 SalesDoc UPDATE quantity for the same product")
    public void testSalesDocUpdateQuantityForTheSameProduct() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        ProductOrderData productOrderData = salesDocument.getProducts().get(0);
        productOrderData.setQuantity(productOrderData.getQuantity() + 2);

        salesDocProductClient.updateSalesDocProducts(salesDocument.getFullDocId(), productOrderData)
                .assertThatIsUpdated(salesDocument, false);
        salesDocProductClient.sendRequestGet(salesDocument.getFullDocId());
        salesDocProductClient.assertThatGetResponseMatches(salesDocument);
    }

    @Test(description = "C22898166 SalesDoc UPDATE price for the same service")
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
    public void testSalesDocUpdateParameterCancelWithProduct() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        salesDocProductClient.cancelSalesDoc(salesDocument.getFullDocId())
                .assertThatIsCancelled(salesDocument);
    }

    @Test(description = "C22898394 SalesDoc UPDATE parameter - Cancel (For document with Services)")
    public void testSalesDocUpdateParameterCancelWithServices() {
        testSalesDocUpdateParameterCancelWithProduct();
    }

}
