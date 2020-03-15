package com.leroy.magmobile.api.builders;

import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.data.sales.cart_estimate.ProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.ServiceOrderData;
import com.leroy.magmobile.api.requests.salesdoc.SalesDocParametersUpdatePut;
import com.leroy.magmobile.api.requests.salesdoc.products.SalesDocProductsGet;
import com.leroy.magmobile.api.requests.salesdoc.products.SalesDocProductsPost;
import com.leroy.magmobile.api.requests.salesdoc.products.SalesDocProductsPut;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Arrays;
import java.util.List;

import static com.leroy.core.matchers.Matchers.valid;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SalesDocProductBuilder extends BaseApiBuilder {

    private Response<SalesDocumentResponseData> response;

    public SalesDocumentResponseData getResponseData() {
        assertThatResponseIsOk(response);
        return response.asJson();
    }

    /**
     * ---------- Executable Requests -------------
     **/

    // GET
    public SalesDocProductBuilder sendRequestGet(String fullDocId) {
        response = apiClient.execute(new SalesDocProductsGet()
                .setFullDocId(fullDocId), SalesDocumentResponseData.class);
        return this;
    }

    // Create (POST)
    private SalesDocProductBuilder sendRequestCreate(SalesDocumentResponseData data) {
        SalesDocProductsPost params = new SalesDocProductsPost();
        params.setShopId(sessionData.getUserShopId())
                .setAccessToken(sessionData.getAccessToken());
        if (sessionData.getRegionId() != null)
            params.setRegionId(sessionData.getRegionId());
        params.setSalesDocumentData(data);
        response = apiClient.execute(params, SalesDocumentResponseData.class);
        return this;
    }

    public SalesDocProductBuilder sendRequestCreate(
            List<ProductOrderData> products, List<ServiceOrderData> services) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setProducts(products);
        salesDocumentResponseData.setServices(services);
        return sendRequestCreate(salesDocumentResponseData);
    }

    public SalesDocProductBuilder sendRequestCreate(
            ProductOrderData... productOrderDataArray) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setProducts(Arrays.asList(productOrderDataArray));
        return sendRequestCreate(salesDocumentResponseData);
    }

    public SalesDocProductBuilder sendRequestCreate(
            ServiceOrderData... serviceOrderDataArray) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setServices(Arrays.asList(serviceOrderDataArray));
        return sendRequestCreate(salesDocumentResponseData);
    }

    // Update (PUT)
    private SalesDocProductBuilder updateSalesDocProducts(String fullDocId,
                                                          SalesDocumentResponseData putSalesDocData) {
        SalesDocProductsPut params = new SalesDocProductsPut();
        params.setFullDocId(fullDocId);
        params.setSalesDocumentData(putSalesDocData);
        params.setShopId(sessionData.getUserShopId())
                .setAccessToken(sessionData.getAccessToken());
        if (sessionData.getRegionId() != null)
            params.setRegionId(sessionData.getRegionId());
        response = apiClient.execute(params, SalesDocumentResponseData.class);

        return this;
    }

    public SalesDocProductBuilder updateSalesDocProducts(String fullDocId,
                                                         ProductOrderData... productOrderData) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setProducts(Arrays.asList(productOrderData));
        return updateSalesDocProducts(fullDocId, salesDocumentResponseData);
    }

    public SalesDocProductBuilder updateSalesDocProducts(String fullDocId,
                                                         ServiceOrderData... serviceOrderData) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setServices(Arrays.asList(serviceOrderData));
        return updateSalesDocProducts(fullDocId, salesDocumentResponseData);
    }

    public SalesDocProductBuilder updateSalesDocProducts(String fullDocId,
                                                         List<ProductOrderData> products, List<ServiceOrderData> services) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setServices(services);
        salesDocumentResponseData.setProducts(products);
        return updateSalesDocProducts(fullDocId, salesDocumentResponseData);
    }

    // Lego_Salesdoc_Parameters_Update
    public SalesDocProductBuilder cancelSalesDoc(String fullDocId) {
        SalesDocParametersUpdatePut params = new SalesDocParametersUpdatePut();
        params.setAccessToken(sessionData.getAccessToken())
                .setLdap(sessionData.getUserLdap())
                .setShopId(sessionData.getUserShopId())
                .setFullDocId(fullDocId)
                .setStatus(SalesDocumentsConst.States.CANCELLED.getApiVal());
        response = apiClient.execute(params, SalesDocumentResponseData.class);
        return this;
    }

    /**
     * ---------- Verifications -------------
     **/

    public SalesDocProductBuilder assertThatIsCreated(boolean isServiceAdded) {
        assertThatResponseIsOk(response);
        SalesDocumentResponseData data = response.asJson();
        assertThat("docId field", data.getDocId(), not(isEmptyOrNullString()));
        assertThat("fullDocId field", data.getFullDocId(),
                allOf(not(isEmptyOrNullString()), endsWith(data.getDocId())));
        if (isServiceAdded) {
            assertThat("newServiceId", data.getNewServiceId(), not(isEmptyOrNullString()));
        }
        assertThat("Document Status", data.getSalesDocStatus(),
                equalTo(SalesDocumentsConst.States.DRAFT.getApiVal()));
        return this;
    }

    public SalesDocProductBuilder assertThatIsUpdated(SalesDocumentResponseData expectedData, boolean isServiceAdded) {
        assertThatResponseIsOk(response);
        SalesDocumentResponseData responseData = response.asJson();
        assertThat("docId field", responseData.getDocId(), is(expectedData.getDocId()));
        assertThat("fullDocId field", responseData.getFullDocId(), is(expectedData.getFullDocId()));
        if (isServiceAdded) {
            assertThat("newServiceId", responseData.getNewServiceId(), not(isEmptyOrNullString()));
            expectedData.getServices().get(0).setId(responseData.getNewServiceId());
        }
        assertThat("Document Status", responseData.getSalesDocStatus(),
                is(expectedData.getSalesDocStatus()));
        return this;
    }

    public SalesDocProductBuilder assertThatIsCancelled(SalesDocumentResponseData expectedData) {
        assertThatResponseIsOk(response);
        SalesDocumentResponseData responseData = response.asJson();
        assertThat("docId field", responseData.getDocId(), is(expectedData.getDocId()));
        assertThat("fullDocId field", responseData.getFullDocId(), is(expectedData.getFullDocId()));
        assertThat("Document Status", responseData.getSalesDocStatus(),
                is(SalesDocumentsConst.States.CANCELLED.getApiVal()));
        return this;
    }

    public SalesDocProductBuilder assertThatGetResponseMatches(SalesDocumentResponseData expectedData) {
        assertThatResponseIsOk(response);
        SalesDocumentResponseData actualData = response.asJson();
        assertThat("FullDocId", actualData.getFullDocId(), equalTo(expectedData.getFullDocId()));
        assertThat("DocId", actualData.getDocId(), equalTo(expectedData.getDocId()));
        assertThat("Document status", actualData.getSalesDocStatus(), equalTo(expectedData.getSalesDocStatus()));
        assertThat("pinCode", actualData.getPinCode(), equalTo(expectedData.getPinCode()));

        assertThat("Products", actualData.getProducts(), hasSize(expectedData.getProducts().size()));
        assertThat("Services", actualData.getServices(), hasSize(expectedData.getServices().size()));

        double docPriceSum = calculateDocSum(expectedData);
        assertThat("docPriceSum",
                actualData.getDocPriceSum(), equalTo(docPriceSum));

        // Products
        List<ProductOrderData> actualProductOrderDataList = actualData.getProducts();
        for (int i = 0; i < actualProductOrderDataList.size(); i++) {
            assertThat("Product order #" + i + " - lm code",
                    actualProductOrderDataList.get(i).getLmCode(),
                    equalTo(expectedData.getProducts().get(i).getLmCode()));
            assertThat("Product order #" + i + " - quantity",
                    actualProductOrderDataList.get(i).getQuantity(),
                    equalTo(expectedData.getProducts().get(i).getQuantity()));
        }

        // Services
        List<ServiceOrderData> actualServiceOrderDataList = actualData.getServices();
        for (int i = 0; i < actualServiceOrderDataList.size(); i++) {
            assertThat("Service order #" + i + " - lm code",
                    actualServiceOrderDataList.get(i).getLmCode(),
                    equalTo(expectedData.getServices().get(i).getLmCode()));
            assertThat("Service order #" + i + " - quantity",
                    actualServiceOrderDataList.get(i).getQuantity(), equalTo(1.0));
            assertThat("Service order #" + i + " - price",
                    actualServiceOrderDataList.get(i).getPrice(),
                    is(expectedData.getServices().get(i).getPrice()));
            assertThat("Service order #" + i + " - id",
                    actualServiceOrderDataList.get(i).getId(),
                    is(expectedData.getNewServiceId()));
            assertThat("Service order #" + i + " - Uom",
                    actualServiceOrderDataList.get(i).getUom(),
                    is(expectedData.getServices().get(i).getUom()));
            assertThat("Service order #" + i + " - Title",
                    actualServiceOrderDataList.get(i).getTitle(),
                    is(expectedData.getServices().get(i).getTitle()));
            assertThat("Service order #" + i + " - BarCode",
                    actualServiceOrderDataList.get(i).getBarCode(),
                    is(expectedData.getServices().get(i).getBarCode()));
            assertThat("Service order #" + i + " - Group Id",
                    actualServiceOrderDataList.get(i).getGroupId(),
                    is(expectedData.getServices().get(i).getGroupId()));
        }
        return this;
    }

    public SalesDocProductBuilder assertThatResponseIsValid() {
        assertThatResponseIsOk(response);
        assertThat(response, valid(SalesDocumentResponseData.class));
        return this;
    }

    // Help methods

    private double calculateDocSum(SalesDocumentResponseData actualData) {
        double docPriceSum = 0;
        for (ProductOrderData productOrderData : actualData.getProducts()) {
            docPriceSum += productOrderData.getQuantity() * productOrderData.getPrice();
        }
        for (ServiceOrderData serviceOrderData : actualData.getServices()) {
            docPriceSum += serviceOrderData.getPrice();
        }
        return docPriceSum;
    }

}
