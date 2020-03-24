package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.data.sales.orders.*;
import com.leroy.magmobile.api.requests.order.*;
import org.json.simple.JSONObject;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void assertThatGetResponseMatches(Response<OrderData> resp, OrderData expectedData) {
        assertThatResponseIsOk(resp);
        OrderData actualData = resp.asJson();
        assertThat("orderId", actualData.getOrderId(), is(expectedData.getOrderId()));
        assertThat("shopId", actualData.getShopId(), is(expectedData.getShopId()));
        assertThat("createdBy", actualData.getCreatedBy(), is(sessionData.getUserLdap()));
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
            assertThat(String.format("Product #%s - lineId", i + 1),
                    actualProduct.getLineId(), is(expectedProduct.getLineId()));
            assertThat(String.format("Product #%s - Quantity", i + 1),
                    actualProduct.getQuantity(), is(expectedProduct.getQuantity()));
            assertThat(String.format("Product #%s - Price", i + 1),
                    actualProduct.getPrice(), is(expectedProduct.getPrice()));
            assertThat(String.format("Product #%s - Type", i + 1),
                    actualProduct.getType(), is(expectedProduct.getType()));
        }
    }

    public void assertThatPinCodeIsSet(Response<JsonNode> resp) {
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
            if (r.isSuccessful() && r.asJson().getStatus().equals(SalesDocumentsConst.States.CONFIRMED.getApiVal())) {
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
                "Status:", r.asJson().getStatus(), is(SalesDocumentsConst.States.CONFIRMED.getApiVal()));
    }


}
