package com.leroy.magmobile.api.tests.salesdoc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.SessionData;
import com.leroy.magmobile.api.helpers.FindTestDataHelper;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.magmobile.models.search.FiltersData;
import com.leroy.magmobile.ui.pages.search.FilterPage;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.ProductItemResponse;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemResponse;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ServiceOrderData;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentResponseData;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SalesDocApiTest extends BaseProjectTest {

    @Inject
    private Provider<MagMobileClient> magMobileClient;

    @Inject
    private AuthClient authClient;

    private ProductOrderData productOrder1;
    private ProductOrderData productOrder2;
    private ServiceOrderData serviceOrder1;
    private ServiceOrderData serviceOrder2;
    private SalesDocumentResponseData salesDocument;

    @BeforeClass
    private void setUpDefaultSessionData() {
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId("35");
        sessionData.setAccessToken(authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
                EnvConstants.BASIC_USER_PASS));
    }

    @Test(description = "C3232445 SalesDoc add product")
    public void testSalesDocAddProduct() {
        // Prepare request data
        productOrder1 = new ProductOrderData(FindTestDataHelper.getProducts(magMobileClient.get(),
                sessionData.getUserShopId(), 1, new FiltersData(FilterPage.MY_SHOP_FRAME_TYPE)).get(0));
        productOrder1.setQuantity((double) new Random().nextInt(6) + 1);

        // Send request
        Response<SalesDocumentResponseData> resp = magMobileClient.get().createSalesDocProducts(
                sessionData, productOrder1);

        // Verifications
        assertThatResponseIsOK(resp);
        salesDocument = resp.asJson();
        assertThat("docId field", salesDocument.getDocId(), not(isEmptyOrNullString()));
        assertThat("fullDocId field", salesDocument.getFullDocId(),
                allOf(not(isEmptyOrNullString()), endsWith(salesDocument.getDocId())));
        assertThat("Document Status", salesDocument.getSalesDocStatus(),
                equalTo(SalesDocumentsConst.States.DRAFT.getApiVal()));
    }

    @Test(description = "C3232446 SalesDoc add services")
    public void testSalesDocAddService() {
        // Prepare request data
        serviceOrder1 = new ServiceOrderData(FindTestDataHelper.getServices(
                magMobileClient.get(), sessionData.getUserShopId(), 1).get(0));
        serviceOrder1.setPrice(446.0);

        // Send request
        Response<SalesDocumentResponseData> resp = magMobileClient.get().createSalesDocProducts(
                sessionData, serviceOrder1);

        // Verifications
        assertThatResponseIsOK(resp);
        salesDocument = resp.asJson();
        assertThat("docId field", salesDocument.getDocId(), not(isEmptyOrNullString()));
        assertThat("fullDocId field", salesDocument.getFullDocId(),
                allOf(not(isEmptyOrNullString()), endsWith(salesDocument.getDocId())));
        assertThat("newServiceId", salesDocument.getNewServiceId(), not(isEmptyOrNullString()));
        assertThat("Document Status", salesDocument.getSalesDocStatus(),
                equalTo(SalesDocumentsConst.States.DRAFT.getApiVal()));
    }

    @Test(description = "C3232447 SalesDoc add product and services")
    public void testSalesDocAddProductAndService() {
        // Prepare request data
        List<ProductItemResponse> productOrderDataList = FindTestDataHelper.getProducts(magMobileClient.get(),
                sessionData.getUserShopId(), 2, new FiltersData(FilterPage.MY_SHOP_FRAME_TYPE));
        productOrder1 = new ProductOrderData(productOrderDataList.get(0));
        productOrder1.setQuantity((double) new Random().nextInt(6) + 1);
        productOrder2 = new ProductOrderData(productOrderDataList.get(1));
        productOrder2.setQuantity((double) new Random().nextInt(6) + 1);

        List<ServiceItemResponse> serviceOrderDataList = FindTestDataHelper.getServices(
                magMobileClient.get(), sessionData.getUserShopId(), 2);
        serviceOrder1 = new ServiceOrderData(serviceOrderDataList.get(0));
        serviceOrder1.setPrice(10.0);
        serviceOrder2 = new ServiceOrderData(serviceOrderDataList.get(1));
        serviceOrder2.setPrice(158.0);

        // Send request
        Response<SalesDocumentResponseData> resp = magMobileClient.get().createSalesDocProducts(sessionData,
                Collections.singletonList(productOrder1),
                Collections.singletonList(serviceOrder1));

        // Verifications
        assertThatResponseIsOK(resp);
        salesDocument = resp.asJson();
        serviceOrder1.setId(salesDocument.getNewServiceId());
        assertThat("docId field", salesDocument.getDocId(), not(isEmptyOrNullString()));
        assertThat("fullDocId field", salesDocument.getFullDocId(),
                allOf(not(isEmptyOrNullString()), endsWith(salesDocument.getDocId())));
        assertThat("newServiceId", salesDocument.getNewServiceId(), not(isEmptyOrNullString()));
        assertThat("Document Status", salesDocument.getSalesDocStatus(),
                equalTo(SalesDocumentsConst.States.DRAFT.getApiVal()));
    }

    @Test(description = "C3232448 SalesDoc product GET")
    public void testSalesDocProductGET() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        Response<SalesDocumentResponseData> resp = magMobileClient.get()
                .getSalesDocProductsByFullDocId(salesDocument.getFullDocId());
        SalesDocumentResponseData data = resp.asJson();
        assertThatResponseIsOK(resp);
        assertThatSalesDocMatches(data);

        assertThat("products", data.getProducts(), hasSize(1));
        ProductOrderData actualProductOrderData = data.getProducts().get(0);
        assertThat("Product order #1 - lm code",
                actualProductOrderData.getLmCode(), equalTo(productOrder1.getLmCode()));
        assertThat("Product order #1 - quantity",
                actualProductOrderData.getQuantity(), equalTo(productOrder1.getQuantity()));
    }

    @Test(description = "C22898131 SalesDoc service GET")
    public void testSalesDocServiceGET() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        Response<SalesDocumentResponseData> resp = magMobileClient.get()
                .getSalesDocProductsByFullDocId(salesDocument.getFullDocId());
        SalesDocumentResponseData data = resp.asJson();
        assertThatResponseIsOK(resp);
        assertThatSalesDocMatches(data);

        assertThat("services", data.getServices(), hasSize(1));
        ServiceOrderData actualServiceOrderData = data.getServices().get(0);
        assertThat("docPriceSum",
                data.getDocPriceSum(), equalTo(serviceOrder1.getPrice()));
        assertThat("Service order #1 - lmCode", actualServiceOrderData.getLmCode(),
                is(serviceOrder1.getLmCode()));
        assertThat("Service order #1 - price",
                actualServiceOrderData.getPrice(), is(serviceOrder1.getPrice()));
        assertThat("Service order #1 - quantity",
                actualServiceOrderData.getQuantity(), is(serviceOrder1.getQuantity()));
        assertThat("Service order #1 - id",
                actualServiceOrderData.getId(), is(salesDocument.getNewServiceId()));
        assertThat("Service order #1 - Uom",
                actualServiceOrderData.getUoM(), is(serviceOrder1.getUoM()));
        assertThat("Service order #1 - Title",
                actualServiceOrderData.getTitle(), is(serviceOrder1.getTitle()));
        assertThat("Service order #1 - BarCode",
                actualServiceOrderData.getBarCode(), is(serviceOrder1.getBarCode()));
        assertThat("Service order #1 - Group Id",
                actualServiceOrderData.getGroupId(), is(serviceOrder1.getGroupId()));
    }

    @Test(description = "C22897826 SalesDoc UPDATE quantity for the same product")
    public void testSalesDocUpdateQuantityForTheSameProduct() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        productOrder1.setQuantity(productOrder1.getQuantity() + 2);
        Response<SalesDocumentResponseData> resp = magMobileClient.get().updateSalesDocProducts(
                sessionData, salesDocument.getFullDocId(), productOrder1);
        assertThatResponseIsOK(resp);
        assertThatSalesDocMatches(resp.asJson());
    }

    @Test(description = "C22898166 SalesDoc UPDATE price for the same service")
    public void testSalesDocUpdatePriceForTheSameService() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        // Change Test data
        serviceOrder1.setPrice(serviceOrder1.getPrice() + 101);

        // Send request
        Response<SalesDocumentResponseData> resp = magMobileClient.get().updateSalesDocProducts(
                sessionData, salesDocument.getFullDocId(), serviceOrder1);

        // Verifications
        assertThatResponseIsOK(resp);
        SalesDocumentResponseData newSalesDocData = resp.asJson();
        assertThat("SalesDoc newServiceId", newSalesDocData.getNewServiceId(),
                not(equalTo(salesDocument.getNewServiceId())));
        serviceOrder1.setId(newSalesDocData.getNewServiceId());
        salesDocument.setNewServiceId(newSalesDocData.getNewServiceId());
        assertThatSalesDocMatches(newSalesDocData);
    }

    @Test(description = "C22898164 SalesDoc UPDATE product and services")
    public void testSalesDocUpdateProductAndService() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        // Prepare request data
        productOrder1.setQuantity((double) new Random().nextInt(6) + 1);
        productOrder2.setQuantity((double) new Random().nextInt(6) + 1);

        serviceOrder1.setPrice(43.0);
        serviceOrder2.setPrice(151.0);

        Response<SalesDocumentResponseData> resp = magMobileClient.get().updateSalesDocProducts(
                sessionData, salesDocument.getFullDocId(), Arrays.asList(productOrder1, productOrder2),
                Arrays.asList(serviceOrder1, serviceOrder2));
        assertThatResponseIsOK(resp);
        assertThatSalesDocMatches(resp.asJson());
    }

    @Test(description = "C22898134 SalesDoc UPDATE parameter - Cancel")
    public void testSalesDocUpdateParameterCancel() {
        if (salesDocument == null)
            throw new IllegalArgumentException("SalesDoc hasn't been created");
        Response<SalesDocumentResponseData> resp = magMobileClient.get().cancelSalesDoc(
                sessionData, salesDocument.getFullDocId());
        assertThatResponseIsOK(resp);
        salesDocument.setSalesDocStatus(SalesDocumentsConst.States.CANCELLED.getApiVal());
        assertThatSalesDocMatches(resp.asJson());
    }

    // Typical asserts

    private void assertThatSalesDocMatches(SalesDocumentResponseData data) {
        assertThat("FullDocId", data.getFullDocId(), equalTo(salesDocument.getFullDocId()));
        assertThat("DocId", data.getDocId(), equalTo(salesDocument.getDocId()));
        assertThat("Document status", data.getSalesDocStatus(), equalTo(salesDocument.getSalesDocStatus()));
        assertThat("pinCode", data.getPinCode(), is(nullValue()));
    }

}
