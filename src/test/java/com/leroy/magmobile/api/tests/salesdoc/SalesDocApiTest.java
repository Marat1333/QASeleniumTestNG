package com.leroy.magmobile.api.tests.salesdoc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.core.SessionData;
import com.leroy.magmobile.api.builders.SalesDocProductBuilder;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.data.sales.cart_estimate.ProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.ServiceOrderData;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SalesDocApiTest extends BaseProjectApiTest {

    @Inject
    private Provider<SalesDocProductBuilder> provider;

    private SalesDocProductBuilder salesDocProductBuilder;

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

        salesDocProductBuilder = provider.get();
        salesDocProductBuilder.setSessionData(sessionData);
    }

    @Test(description = "C3232445 SalesDoc add product")
    public void testSalesDocAddProduct() {
        // Prepare request data
        ProductOrderData productOrderData = salesDocProductBuilder.findProducts(1).get(0);
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);

        // Create and check
        salesDocProductBuilder.sendRequestCreate(productOrderData)
                .assertThatIsCreated(false);

        // Get data
        salesDocument = salesDocProductBuilder.getResponseData();
        salesDocument.setProducts(Collections.singletonList(productOrderData));

        // Send get
        salesDocProductBuilder.sendRequestGet(salesDocument.getFullDocId());
        salesDocProductBuilder.assertThatGetResponseMatches(salesDocument);
    }

    @Test(description = "C3232446 SalesDoc add services")
    public void testSalesDocAddService() {
        // Prepare request data
        ServiceOrderData serviceOrderData = salesDocProductBuilder.findServices(1).get(0);
        serviceOrderData.setPrice(10.0);

        // Send requests and verification
        salesDocProductBuilder.sendRequestCreate(serviceOrderData)
                .assertThatIsCreated(true);

        // Get data
        salesDocument = salesDocProductBuilder.getResponseData();
        salesDocument.setServices(Collections.singletonList(serviceOrderData));

        salesDocProductBuilder.sendRequestGet(salesDocument.getFullDocId())
                .assertThatGetResponseMatches(salesDocument);
    }

    @Test(description = "C3232447 SalesDoc add product and services", enabled = false)
    public void testSalesDocAddProductAndService() {
        // Prepare request data
        List<ProductOrderData> productOrderDataList = salesDocProductBuilder.findProducts(2);
        productOrderDataList.get(0).setQuantity((double) new Random().nextInt(6) + 1);
        productOrderDataList.get(1).setQuantity((double) new Random().nextInt(6) + 1);

        List<ServiceOrderData> serviceOrderDataList = salesDocProductBuilder.findServices(1);
        serviceOrderDataList.get(0).setPrice(10.0);

        // Send requests and verifications
        salesDocProductBuilder.sendRequestCreate(productOrderDataList, serviceOrderDataList)
                .assertThatIsCreated(true);
        salesDocument = salesDocProductBuilder.getResponseData();
        salesDocument.setProducts(productOrderDataList);
        salesDocument.setServices(serviceOrderDataList);

        salesDocProductBuilder.sendRequestGet(salesDocument.getFullDocId())
                .assertThatGetResponseMatches(salesDocument);
    }

    @Test(description = "C3232448 SalesDoc product GET")
    public void testSalesDocProductGET() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        salesDocProductBuilder.sendRequestGet(salesDocument.getFullDocId())
                .assertThatResponseIsValid();
    }

    @Test(description = "C22898131 SalesDoc service GET")
    public void testSalesDocServiceGET() throws Exception {
        testSalesDocProductGET();
    }

    // TODO Может оставить только этот?
    @Test(description = "C22898132 SalesDoc product and service GET", enabled = false)
    public void testSalesDocProductAndServiceGET() throws Exception {
        testSalesDocProductGET();
    }

    @Test(description = "C22897826 SalesDoc UPDATE quantity for the same product")
    public void testSalesDocUpdateQuantityForTheSameProduct() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        ProductOrderData productOrderData = salesDocument.getProducts().get(0);
        productOrderData.setQuantity(productOrderData.getQuantity() + 2);

        salesDocProductBuilder.updateSalesDocProducts(salesDocument.getFullDocId(), productOrderData)
                .assertThatIsUpdated(salesDocument, false);
        salesDocProductBuilder.sendRequestGet(salesDocument.getFullDocId());
        salesDocProductBuilder.assertThatGetResponseMatches(salesDocument);
    }

    @Test(description = "C22898166 SalesDoc UPDATE price for the same service")
    public void testSalesDocUpdatePriceForTheSameService() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        ServiceOrderData serviceOrderData = salesDocument.getServices().get(0);
        serviceOrderData.setPrice(serviceOrderData.getPrice() + 101);
        salesDocProductBuilder.updateSalesDocProducts(salesDocument.getFullDocId(), serviceOrderData)
                .assertThatIsUpdated(salesDocument, true);

        salesDocument.setNewServiceId(salesDocProductBuilder.getResponseData().getNewServiceId());

        salesDocProductBuilder.sendRequestGet(salesDocument.getFullDocId())
                .assertThatGetResponseMatches(salesDocument);
    }

    @Test(description = "C22898164 SalesDoc UPDATE product and services", enabled = false)
    public void testSalesDocUpdateProductAndService() {
        /*if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        salesDocument.getProducts().get(0).setQuantity(
                salesDocument.getProducts().get(0).getQuantity() + 2);
        salesDocument.getProducts().get(1).setQuantity(
                salesDocument.getProducts().get(0).getQuantity() + 3);

        salesDocProductBuilder.updateSalesDocProducts(salesDocument.getFullDocId(), productOrderData)
                .assertThatIsUpdated(salesDocument, false);
        salesDocProductBuilder.sendRequestGet(salesDocument.getFullDocId());
        salesDocProductBuilder.assertThatGetResponseMatches(salesDocument);

        /////////////
        ServiceOrderData serviceOrderData = salesDocument.getServices().get(0);
        serviceOrderData.setPrice(serviceOrderData.getPrice() + 101);
        salesDocProductBuilder.updateSalesDocProducts(salesDocument.getFullDocId(), serviceOrderData)
                .assertThatIsUpdated(salesDocument, true);

        salesDocument.setNewServiceId(salesDocProductBuilder.getResponseData().getNewServiceId());

        salesDocProductBuilder.sendRequestGet(salesDocument.getFullDocId())
                .assertThatGetResponseMatches(salesDocument);

        // Prepare request data
        productOrder1.setQuantity((double) new Random().nextInt(6) + 1);
        productOrder2.setQuantity((double) new Random().nextInt(6) + 1);

        serviceOrder1.setPrice(43.0);
        serviceOrder2.setPrice(151.0);

        Response<SalesDocumentResponseData> resp = magMobileClient.get().updateSalesDocProducts(
                sessionData, salesDocument.getFullDocId(), Arrays.asList(productOrder1, productOrder2),
                Arrays.asList(serviceOrder1, serviceOrder2));
        assertThat(resp, successful());
        assertThatSalesDocMatches(resp.asJson());*/
    }

    @Test(description = "C22898134 SalesDoc UPDATE parameter - Cancel (For document with Products)")
    public void testSalesDocUpdateParameterCancelWithProduct() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        salesDocProductBuilder.cancelSalesDoc(salesDocument.getFullDocId())
                .assertThatIsCancelled(salesDocument);
    }

    @Test(description = "C22898394 SalesDoc UPDATE parameter - Cancel (For document with Services)")
    public void testSalesDocUpdateParameterCancelWithServices() {
        testSalesDocUpdateParameterCancelWithProduct();
    }

    @Test(description = "C22898395 SalesDoc UPDATE parameter - Cancel (For document with Products and Services)")
    public void testSalesDocUpdateParameterCancelWithProductsAndServices() {
        testSalesDocUpdateParameterCancelWithProduct();
    }

}
