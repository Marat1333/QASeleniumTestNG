package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateData;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.SendEmailData;
import com.leroy.magmobile.api.requests.salesdoc.estimate.*;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.*;

import static com.leroy.core.matchers.Matchers.isNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EstimateClient extends MagMobileClient {

    /**
     * ---------- Executable Requests -------------
     **/

    public Response<EstimateData> sendRequestGet(String estimateId) {
        return execute(new EstimateGet().setEstimateId(estimateId)
                .bearerAuthHeader(sessionData.getAccessToken())
                .setShopId(sessionData.getUserShopId()), EstimateData.class);
    }

    public Response<EstimateData> sendRequestCreate(List<CartEstimateProductOrderData> productOrderDataList) {
        List<CartEstimateProductOrderData> filteredProducts = new ArrayList<>();
        for (CartEstimateProductOrderData prData : productOrderDataList) {
            CartEstimateProductOrderData filterPrData = new CartEstimateProductOrderData();
            filterPrData.setQuantity(prData.getQuantity());
            filterPrData.setLmCode(prData.getLmCode());
            filteredProducts.add(filterPrData);
        }
        EstimateData estimateData = new EstimateData();
        estimateData.setProducts(filteredProducts);
        return execute(new EstimatePost()
                .bearerAuthHeader(sessionData.getAccessToken())
                .setShopId(sessionData.getUserShopId())
                .jsonBody(estimateData), EstimateData.class);
    }

    public Response<EstimateData> sendRequestCreate(
            CartEstimateProductOrderData productOrderData) {
        return sendRequestCreate(Collections.singletonList(productOrderData));
    }

    public Response<EstimateData> sendRequestUpdate(String estimateId,
                                                    List<CartEstimateProductOrderData> productOrderDataList) {
        EstimateData estimateData = new EstimateData();
        estimateData.setProducts(productOrderDataList);
        return execute(new EstimatePut()
                .setEstimateId(estimateId)
                .bearerAuthHeader(sessionData.getAccessToken())
                .setShopId(sessionData.getUserShopId())
                .jsonBody(estimateData), EstimateData.class);
    }

    public Response<EstimateData> sendRequestUpdate(String estimateId,
            CartEstimateProductOrderData productOrderData) {
        return sendRequestUpdate(estimateId, Collections.singletonList(productOrderData));
    }

    public Response<JsonNode> sendRequestDelete(String estimateId, int documentVersion) {
        Map<String, String> body = new HashMap<>();
        body.put("status", SalesDocumentsConst.States.DELETED.getApiVal());
        body.put("documentVersion", String.valueOf(documentVersion));
        return execute(new EstimateChangeStatusPut()
                .bearerAuthHeader(sessionData.getAccessToken())
                .setEstimateId(estimateId)
                .formBody(body), JsonNode.class);
    }

    public Response<JsonNode> sendEmail(String estimateId, SendEmailData emailData) {
        EstimateSendEmailRequest req = new EstimateSendEmailRequest();
        req.setEstimateId(estimateId);
        req.jsonBody(emailData);
        return execute(req, JsonNode.class);
    }

    /**
     * ------------  Verifications -----------------
     **/
    public EstimateData assertThatIsCreatedAndGetData(Response<EstimateData> response) {
        assertThatResponseIsOk(response);
        EstimateData data = response.asJson();
        assertThat("fullDocId", data.getFullDocId(), isNumber());
        assertThat("estimateId", data.getEstimateId(), is(data.getFullDocId()));
        assertThat("docType", data.getDocType(), is(SalesDocumentsConst.Types.QUOTATION.getApiVal()));
        assertThat("salesDocStatus", data.getSalesDocStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("documentType", data.getDocumentType(), is(SalesDocumentsConst.Types.QUOTATION.getApiVal()));
        assertThat("status", data.getStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("shopId", data.getShopId(), is(sessionData.getUserShopId()));
        assertThat("documentVersion", data.getDocumentVersion(), is(1));

        assertThat("products", data.getProducts(), hasSize(greaterThan(0)));
        assertThat("customers", data.getCustomers(), not(nullValue()));

        return data;
    }

    private void shortVerifyProducts(
            int i, CartEstimateProductOrderData actualProduct, CartEstimateProductOrderData expectedProduct) {
        assertThat(String.format("Product #%s - lmCode", i + 1),
                actualProduct.getLmCode(), is(expectedProduct.getLmCode()));
        assertThat(String.format("Product #%s - Quantity", i + 1),
                actualProduct.getQuantity(), is(expectedProduct.getQuantity()));
        assertThat(String.format("Product #%s - Price", i + 1),
                actualProduct.getPrice(), is(expectedProduct.getPrice()));
        assertThat(String.format("Product #%s - Type", i + 1),
                actualProduct.getType(), is(expectedProduct.getType()));
    }

    public EstimateClient assertThatResponseContainsAddedProducts(
            Response<EstimateData> resp, List<CartEstimateProductOrderData> expectedProducts) {
        assertThatResponseIsOk(resp);
        EstimateData actualData = resp.asJson();
        for (int i = 0; i < actualData.getProducts().size(); i++) {
            CartEstimateProductOrderData actualProduct = actualData.getProducts().get(i);
            CartEstimateProductOrderData expectedProduct = expectedProducts.get(i);
            shortVerifyProducts(i, actualProduct, expectedProduct);
        }
        return this;
    }

    public EstimateClient assertThatGetResponseMatches(Response<EstimateData> resp, EstimateData expectedData) {
        assertThatResponseIsOk(resp);
        EstimateData actualData = resp.asJson();
        assertThat("FullDocId", actualData.getFullDocId(), equalTo(expectedData.getFullDocId()));
        assertThat("docType", actualData.getDocType(), is(expectedData.getDocType()));
        assertThat("salesDocStatus", actualData.getSalesDocStatus(), is(expectedData.getSalesDocStatus()));
        assertThat("documentType", actualData.getDocumentType(), is(expectedData.getDocumentType()));
        assertThat("status", actualData.getStatus(), is(expectedData.getStatus()));
        assertThat("shopId", actualData.getShopId(), is(expectedData.getShopId()));
        assertThat("cartId", actualData.getEstimateId(), is(expectedData.getEstimateId()));
        assertThat("documentVersion", actualData.getDocumentVersion(), is(expectedData.getDocumentVersion()));

        assertThat("products", actualData.getProducts(), hasSize(expectedData.getProducts().size()));

        for (int i = 0; i < actualData.getProducts().size(); i++) {
            CartEstimateProductOrderData actualProduct = actualData.getProducts().get(i);
            CartEstimateProductOrderData expectedProduct = expectedData.getProducts().get(i);
            shortVerifyProducts(i, actualProduct, expectedProduct);
            assertThat(String.format("Product #%s - barCode", i + 1),
                    actualProduct.getBarCode(), is(expectedProduct.getBarCode()));
            assertThat(String.format("Product #%s - priceCategory", i + 1),
                    actualProduct.getBarCode(), not(nullValue()));
            assertThat(String.format("Product #%s - lineId", i + 1),
                    actualProduct.getLineId(), is(expectedProduct.getLineId()));
        }
        return this;
    }

    public void assertThatResponseChangeStatusIsOk(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        assertThat("result", resp.asJson().get("result").asText(), is("OK"));
    }


}
