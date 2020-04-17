package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.*;
import com.leroy.magmobile.api.requests.order.*;
import org.json.simple.JSONObject;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.*;

import static com.leroy.core.matchers.Matchers.isNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OrderClient extends MagMobileClient {

    /**
     * ---------- Executable Requests -------------
     **/

    public Response<OrderData> getOrder(String orderId) {
        OrderGet req = new OrderGet();
        req.setOrderId(orderId);
        return execute(req, OrderData.class);
    }

    public Response<OrderData> createOrder(ReqOrderData reqOrderData) {
        OrderPost orderPost = new OrderPost();
        orderPost.bearerAuthHeader(sessionData.getAccessToken());
        orderPost.jsonBody(reqOrderData);
        orderPost.setShopId(sessionData.getUserShopId());
        orderPost.setUserLdap(sessionData.getUserLdap());
        return execute(orderPost, OrderData.class);
    }

    public Response<OrderData> confirmOrder(String orderId, OrderData putOrderData) {
        OrderConfirmRequest req = new OrderConfirmRequest();
        req.setShopId(sessionData.getUserShopId());
        req.setUserLdap(sessionData.getUserLdap());
        req.setOrderId(orderId);
        req.jsonBody(putOrderData);
        return execute(req, OrderData.class);
    }

    public Response<ResOrderCheckQuantityData> checkQuantity(ReqOrderData data) {
        OrderCheckQuantityRequest req = new OrderCheckQuantityRequest();
        req.setShopId(sessionData.getUserShopId());
        req.jsonBody(data);
        return execute(req, ResOrderCheckQuantityData.class);
    }

    public Response<JsonNode> setPinCode(String orderId, String pinCode) {
        OrderSetPinCodeRequest req = new OrderSetPinCodeRequest();
        req.setOrderId(orderId);
        Map<String, String> body = new HashMap<>();
        body.put("pinCode", pinCode);
        req.jsonBody(body);
        return execute(req, JsonNode.class);
    }

    public Response<OrderData> updateDraftOrder(OrderData orderData) {
        OrderPutRequest req = new OrderPutRequest();
        req.setShopId(sessionData.getUserShopId());
        req.setUserLdap(sessionData.getUserLdap());
        req.jsonBody(orderData);
        return execute(req, OrderData.class);
    }

    public Response<JsonNode> rearrange(OrderData orderData,
                                        BaseProductOrderData productData) {
        OrderRearrangeRequest req = new OrderRearrangeRequest();
        req.setOrderId(orderData.getOrderId());
        req.setShopId(sessionData.getUserShopId());
        req.setUserLdap(sessionData.getUserLdap());
        OrderData putOrderData = new OrderData();
        putOrderData.setSolutionVersion(orderData.getSolutionVersion());
        putOrderData.setPaymentVersion(orderData.getPaymentVersion());
        putOrderData.setSolutionVersion(orderData.getSolutionVersion());
        putOrderData.setFulfillmentVersion(orderData.getFulfillmentVersion());
        putOrderData.setFulfillmentTaskId(orderData.getFulfillmentTaskId());
        putOrderData.setPaymentTaskId(orderData.getPaymentTaskId());
        putOrderData.setGiveAway(orderData.getGiveAway());

        OrderProductData putProductData = new OrderProductData();
        putProductData.setLmCode(productData.getLmCode());
        putProductData.setQuantity(productData.getQuantity());
        putProductData.setType(productData.getType());
        putProductData.setPrice(productData.getPrice());
        putOrderData.setProducts(Collections.singletonList(putProductData));
        req.jsonBody(putOrderData);
        return execute(req, JsonNode.class);
    }

    public Response<JsonNode> deleteDraftOrder(String orderId) {
        OrderChangeStatusRequest req = new OrderChangeStatusRequest();
        req.setOrderId(orderId);
        req.setUserLdap(sessionData.getUserLdap());
        Map<String, String> body = new HashMap<>();
        body.put("status", SalesDocumentsConst.States.DELETED.getApiVal());
        req.jsonBody(body);
        return execute(req, JsonNode.class);
    }

    public Response<JsonNode> cancelOrder(String orderId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "cancel-order");
        return execute(new OrderWorkflowPut()
                .setOrderId(orderId)
                .setUserLdap(sessionData.getUserLdap())
                .jsonBody(jsonObject), JsonNode.class);
    }

    /**
     * ------------  Verifications -----------------
     **/
    public OrderData assertThatIsCreatedAndGetData(Response<OrderData> response) {
        assertThatResponseIsOk(response);
        OrderData data = response.asJson();
        assertThat("fullDocId", data.getFullDocId(), isNumber());
        assertThat("orderId", data.getOrderId(), is(data.getFullDocId()));
        assertThat("docType", data.getDocType(), is(SalesDocumentsConst.Types.ORDER.getApiVal()));
        assertThat("salesDocStatus", data.getSalesDocStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("status", data.getStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("shopId", data.getShopId(), is(sessionData.getUserShopId()));
        assertThat("fulfillmentTaskId", data.getFulfillmentTaskId(), not(emptyOrNullString()));
        assertThat("paymentTaskId", data.getPaymentTaskId(), not(emptyOrNullString()));

        assertThat("customers", data.getCustomers(), not(nullValue()));
        assertThat("products", data.getProducts(), hasSize(greaterThan(0)));

        return data;
    }

    public OrderClient assertThatResponseContainsAddedProducts(
            Response<OrderData> resp, List<ReqOrderProductData> expectedProducts) {
        assertThatResponseIsOk(resp);
        OrderData actualData = resp.asJson();
        for (int i = 0; i < actualData.getProducts().size(); i++) {
            OrderProductData actualProduct = actualData.getProducts().get(i);
            ReqOrderProductData expectedProduct = expectedProducts.get(i);
            assertThat(String.format("Product #%s - lmCode", i + 1),
                    actualProduct.getLmCode(), is(expectedProduct.getLmCode()));
            assertThat(String.format("Product #%s - lineId", i + 1),
                    actualProduct.getLineId(), is(expectedProduct.getLineId()));
            assertThat(String.format("Product #%s - Quantity", i + 1),
                    actualProduct.getQuantity(), is(expectedProduct.getQuantity()));
            assertThat(String.format("Product #%s - Price", i + 1),
                    actualProduct.getPrice(), is(expectedProduct.getPrice()));
            assertThat(String.format("Product #%s - Type", i + 1),
                    actualProduct.getType(), is(expectedProduct.getType()));
        }
        return this;
    }

    public void assertThatResponseMatches(Response<OrderData> resp, OrderData expectedData) {
        assertThatResponseMatches(resp, expectedData, ResponseType.GET, true);
    }

    public void assertThatResponseMatches(Response<OrderData> resp, OrderData expectedData, ResponseType responseType) {
        assertThatResponseMatches(resp, expectedData, responseType, true);
    }

    public void assertThatResponseMatches(Response<OrderData> resp, OrderData expectedData, ResponseType responseType,
                                          boolean checkProductLineId) {
        assertThatResponseIsOk(resp);
        OrderData actualData = resp.asJson();
        assertThat("orderId", actualData.getOrderId(), is(expectedData.getOrderId()));
        assertThat("shopId", actualData.getShopId(), is(expectedData.getShopId()));
        if (!ResponseType.PUT.equals(responseType)) {
            assertThat("createdBy", actualData.getCreatedBy(), is(sessionData.getUserLdap()));
        }
        assertThat("fulfillmentTaskId", actualData.getFulfillmentTaskId(),
                is(expectedData.getFulfillmentTaskId()));
        //assertThat("fulfillmentVersion", actualData.getFulfillmentVersion(),
        //        is(expectedData.getFulfillmentVersion())); // TODO иногда при изменении документа увеличивается на 2.
        assertThat("paymentTaskId", actualData.getPaymentTaskId(),
                is(expectedData.getPaymentTaskId()));
        //assertThat("paymentVersion", actualData.getPaymentVersion(),
        //        is(expectedData.getPaymentVersion())); // TODO - разобраться что это такое
        if (expectedData.getStatus().equals(SalesDocumentsConst.States.CANCELLED.getApiVal())) {
            assertThat("status", actualData.getStatus(),
                    oneOf(expectedData.getStatus(), "CANCELLATION_IN_PROGRESS"));
        } else {
            assertThat("status", actualData.getStatus(),
                    is(expectedData.getStatus()));
        }

        assertThat("products", actualData.getProducts(), hasSize(expectedData.getProducts().size()));

        for (int i = 0; i < actualData.getProducts().size(); i++) {
            OrderProductData actualProduct = actualData.getProducts().get(i);
            OrderProductData expectedProduct = expectedData.getProducts().get(i);
            assertThat(String.format("Product #%s - lmCode", i + 1),
                    actualProduct.getLmCode(), is(expectedProduct.getLmCode()));
            if (checkProductLineId) {
                assertThat(String.format("Product #%s - lineId", i + 1),
                        actualProduct.getLineId(), is(expectedProduct.getLineId()));
            } else {
                assertThat(String.format("Product #%s - lineId", i + 1),
                        actualProduct.getLineId(), not(emptyOrNullString()));
            }
            if (!ResponseType.DELETE.equals(responseType)) {
                assertThat(String.format("Product #%s - Quantity", i + 1),
                        actualProduct.getQuantity(), is(expectedProduct.getQuantity()));
            }
            assertThat(String.format("Product #%s - Price", i + 1),
                    actualProduct.getPrice(), is(expectedProduct.getPrice()));
            assertThat(String.format("Product #%s - Type", i + 1),
                    actualProduct.getType(), is(expectedProduct.getType()));
        }
    }

    public void assertThatPinCodeIsSet(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
    }

    public void assertThatRearranged(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
    }

    public void assertThatIsConfirmed(Response<OrderData> resp, OrderData expectedData) {
        assertThatResponseIsOk(resp);
        OrderData actualData = resp.asJson();
        assertThat("orderId", actualData.getOrderId(), is(expectedData.getOrderId()));
        assertThat("fullDocId", actualData.getFullDocId(), is(expectedData.getFullDocId()));
        assertThat("status", actualData.getStatus(), is(SalesDocumentsConst.States.IN_PROGRESS.getApiVal()));
        assertThat("salesDocStatus", actualData.getSalesDocStatus(),
                is(SalesDocumentsConst.States.IN_PROGRESS.getApiVal()));
    }

    public void assertThatCheckQuantityIsOk(Response<ResOrderCheckQuantityData> resp,
                                            List<ReqOrderProductData> expectedProductDataList) {
        assertThatResponseIsOk(resp);
        ResOrderCheckQuantityData actualData = resp.asJson();
        assertThat("result", actualData.getResult(), is("OK"));
        assertThat("groupingId", actualData.getGroupingId(), is("ON_ORDER"));
        assertThat("product size", actualData.getProducts(), hasSize(expectedProductDataList.size()));
        for (int i = 0; i < actualData.getProducts().size(); i++) {
            ResOrderProductCheckQuantityData actualProduct = actualData.getProducts().get(i);
            ReqOrderProductData expectedProduct = expectedProductDataList.get(i);
            assertThat("Product " + (i + 1) + " lmCode", actualProduct.getLmCode(), is(expectedProduct.getLmCode()));
            assertThat("Product " + (i + 1) + " quantity", actualProduct.getQuantity(),
                    is(expectedProduct.getQuantity()));
            assertThat("Product " + (i + 1) + " lineId", actualProduct.getLineId(), not(emptyOrNullString()));
            assertThat("Product " + (i + 1) + " title", actualProduct.getTitle(), not(emptyOrNullString()));
            assertThat("Product " + (i + 1) + " barCode", actualProduct.getBarCode(), not(emptyOrNullString()));
        }
    }

    public OrderClient assertThatIsCancelled(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        JsonNode respData = resp.asJson();
        assertThat("status", respData.get("status").asText(),
                is("CONFIRMED"));
        return this;
    }

    public void assertThatResponseChangeStatusIsOk(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        assertThat("result", resp.asJson().get("result").asText(), is("OK"));
    }


    // ------- Help Method ---------- //

    /**
     * Wait until Order status is Confirmed
     *
     * @param orderId - order Id
     */
    public void waitUntilOrderIsConfirmed(String orderId) throws Exception {
        int maxTimeoutInSeconds = 60;
        long currentTimeMillis = System.currentTimeMillis();
        Response<OrderData> r = null;
        while (System.currentTimeMillis() - currentTimeMillis < maxTimeoutInSeconds * 1000) {
            r = getOrder(orderId);
            if (r.isSuccessful() && !r.asJson().getStatus()
                    .equals(SalesDocumentsConst.States.IN_PROGRESS.getApiVal())) {
                Log.info("waitUntilOrderIsConfirmed() has executed for " +
                        (System.currentTimeMillis() - currentTimeMillis) / 1000 + " seconds");
                return;
            }
            Thread.sleep(3000);
        }
        assertThat("Could not wait for the order to be confirmed. Timeout=" + maxTimeoutInSeconds + ". " +
                        "Response error:" + r.toString(),
                r.isSuccessful());
        assertThat("Could not wait for the order to be confirmed. Timeout=" + maxTimeoutInSeconds + ". " +
                "Status:", r.asJson().getStatus(),
                not(SalesDocumentsConst.States.IN_PROGRESS.getApiVal()));
    }


}