package com.leroy.magmobile.api.clients;

import static com.leroy.core.matchers.Matchers.isNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.oneOf;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderCustomerData;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderProductData;
import com.leroy.magmobile.api.data.sales.orders.ReqOrderData;
import com.leroy.magmobile.api.data.sales.orders.ReqOrderProductData;
import com.leroy.magmobile.api.data.sales.orders.ResOrderCheckQuantityData;
import com.leroy.magmobile.api.data.sales.orders.ResOrderProductCheckQuantityData;
import com.leroy.magmobile.api.requests.order.OrderChangeStatusRequest;
import com.leroy.magmobile.api.requests.order.OrderCheckQuantityRequest;
import com.leroy.magmobile.api.requests.order.OrderConfirmRequest;
import com.leroy.magmobile.api.requests.order.OrderGet;
import com.leroy.magmobile.api.requests.order.OrderPost;
import com.leroy.magmobile.api.requests.order.OrderPutRequest;
import com.leroy.magmobile.api.requests.order.OrderRearrangeRequest;
import com.leroy.magmobile.api.requests.order.OrderSetPinCodeRequest;
import com.leroy.magmobile.api.requests.order.OrderWorkflowPut;
import io.qameta.allure.Step;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import ru.leroymerlin.qa.core.clients.base.Response;

public class OrderClient extends BaseMashupClient {

    /**
     * ---------- Executable Requests -------------
     *
     * @return*/
    private String paoUrl;



    @Override
    protected void init() {
        gatewayUrl = EnvConstants.MAIN_API_HOST;
        paoUrl = EnvConstants.PAO_API_HOST;
        jaegerHost = EnvConstants.PICK_JAEGER_HOST;
        jaegerService = EnvConstants.PICK_JAEGER_SERVICE;
    }

    @Step("Get order with id = {orderId}")
    public Response<OrderData> getOrder(String orderId) {
        OrderGet req = new OrderGet();
        req.setOrderId(orderId);
        return execute(req, OrderData.class);
    }

    @Step("Create order")
    public Response<OrderData> createOrder(ReqOrderData reqOrderData) {
        OrderPost orderPost = new OrderPost();
        orderPost.bearerAuthHeader(getUserSessionData().getAccessToken());
        orderPost.jsonBody(reqOrderData);
        orderPost.setShopId(getUserSessionData().getUserShopId());
        orderPost.setUserLdap(getUserSessionData().getUserLdap());
        return execute(orderPost, OrderData.class, paoUrl);
    }

    @Step("Confirm order with id = {orderId}")
    public Response<OrderData> confirmOrder(String orderId, OrderData putOrderData) {
        OrderConfirmRequest req = new OrderConfirmRequest();
        req.setShopId(getUserSessionData().getUserShopId());
        req.setUserLdap(getUserSessionData().getUserLdap());
        req.setOrderId(orderId);
        req.jsonBody(putOrderData);
        return execute(req, OrderData.class, paoUrl);
    }

    @Step("Check quantity")
    public Response<ResOrderCheckQuantityData> checkQuantity(ReqOrderData data) {
        OrderCheckQuantityRequest req = new OrderCheckQuantityRequest();
        req.setShopId(getUserSessionData().getUserShopId());
        req.jsonBody(data);
        return execute(req, ResOrderCheckQuantityData.class);
    }

    @Step("Set {pinCode} PIN code for order with id = {orderId}")
    public Response<JsonNode> setPinCode(String orderId, String pinCode) {
        OrderSetPinCodeRequest req = new OrderSetPinCodeRequest();
        req.setOrderId(orderId);
        Map<String, String> body = new HashMap<>();
        body.put("pinCode", pinCode);
        req.jsonBody(body);
        return execute(req, JsonNode.class, paoUrl);
    }

    @Step("Update Order")
    public Response<OrderData> updateDraftOrder(OrderData orderData) {
        OrderPutRequest req = new OrderPutRequest();
        req.setShopId(getUserSessionData().getUserShopId());
        req.setUserLdap(getUserSessionData().getUserLdap());
        req.jsonBody(orderData);
        return execute(req, OrderData.class, paoUrl);
    }

    @Step("Rearrange order")
    public Response<JsonNode> rearrange(OrderData orderData,
            BaseProductOrderData productData) {
        OrderRearrangeRequest req = new OrderRearrangeRequest();
        req.setOrderId(orderData.getOrderId());
        req.setShopId(getUserSessionData().getUserShopId());
        req.setUserLdap(getUserSessionData().getUserLdap());
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
        return execute(req, JsonNode.class, paoUrl);
    }

    @Step("Make status DELETED for order with id = {orderId}")
    public Response<JsonNode> deleteDraftOrder(String orderId) {
        OrderChangeStatusRequest req = new OrderChangeStatusRequest();
        req.setOrderId(orderId);
        req.setUserLdap(getUserSessionData().getUserLdap());
        Map<String, String> body = new HashMap<>();
        body.put("status", SalesDocumentsConst.States.DELETED.getApiVal());
        req.jsonBody(body);
        return execute(req, JsonNode.class, paoUrl);
    }

    @Step("Cancel order with id = {orderId}")
    public Response<JsonNode> cancelOrder(String orderId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "cancel-order");
        return execute(new OrderWorkflowPut()
                .setOrderId(orderId)
                .setUserLdap(getUserSessionData().getUserLdap())
                .jsonBody(jsonObject), JsonNode.class);
    }

    /**
     * ------------  Verifications -----------------
     **/
    @Step("Check that order is created and response has valid data")
    public OrderData assertThatIsCreatedAndGetData(Response<OrderData> response) {
        assertThatResponseIsOk(response);
        OrderData data = response.asJson();
        assertThat("fullDocId", data.getFullDocId(), isNumber());
        assertThat("orderId", data.getOrderId(), is(data.getFullDocId()));
        assertThat("docType", data.getDocType(), is(SalesDocumentsConst.Types.ORDER.getApiVal()));
        assertThat("salesDocStatus", data.getSalesDocStatus(),
                is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("status", data.getStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("shopId", data.getShopId(), is(getUserSessionData().getUserShopId()));
        assertThat("fulfillmentTaskId", data.getFulfillmentTaskId(), not(emptyOrNullString()));
        assertThat("paymentTaskId", data.getPaymentTaskId(), not(emptyOrNullString()));

        assertThat("customers", data.getCustomers(), not(nullValue()));
        assertThat("products", data.getProducts(), hasSize(greaterThan(0)));

        return data;
    }

    @Step("Check that Response body contains products: {expectedProducts}")
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

    public void assertThatResponseMatches(Response<OrderData> resp, OrderData expectedData,
            ResponseType responseType) {
        assertThatResponseMatches(resp, expectedData, responseType, true);
    }

    @Step("Check that Response body matches expectedData")
    public void assertThatResponseMatches(Response<OrderData> resp, OrderData expectedData,
            ResponseType responseType,
            boolean checkProductLineId) {
        assertThatResponseIsOk(resp);
        OrderData actualData = resp.asJson();
        assertThat("orderId", actualData.getOrderId(), is(expectedData.getOrderId()));
        assertThat("shopId", actualData.getShopId(), is(expectedData.getShopId()));
        if (!ResponseType.PUT.equals(responseType)) {
            assertThat("createdBy", actualData.getCreatedBy(),
                    is(getUserSessionData().getUserLdap()));
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

        assertThat("Customers count", actualData.getCustomers(),
                hasSize(expectedData.getCustomers().size()));
        for (int i = 0; i < actualData.getCustomers().size(); i++) {
            OrderCustomerData actualCustomer = actualData.getCustomers().get(i);
            OrderCustomerData expectedCustomer = expectedData.getCustomers().get(i);
            assertThat(String.format("Customer #%s - customerNumber", i + 1),
                    actualCustomer.getCustomerNumber(), is(expectedCustomer.getCustomerNumber()));
            assertThat(String.format("Customer #%s - firstName", i + 1),
                    actualCustomer.getFirstName(), is(expectedCustomer.getFirstName()));
            assertThat(String.format("Customer #%s - lastName", i + 1),
                    actualCustomer.getLastName(), is(expectedCustomer.getLastName()));
            //assertThat(String.format("Customer #%s - fullName", i + 1),
            //        actualCustomer.getFullName(), is(expectedCustomer.getFullName()));
            assertThat(String.format("Customer #%s - Phone Data", i + 1),
                    actualCustomer.getPhone(), is(expectedCustomer.getPhone()));
            assertThat(String.format("Customer #%s - email", i + 1),
                    actualCustomer.getEmail(), is(expectedCustomer.getEmail()));
            assertThat(String.format("Customer #%s - roles", i + 1),
                    actualCustomer.getRoles(), is(expectedCustomer.getRoles()));
            assertThat(String.format("Customer #%s - type", i + 1),
                    actualCustomer.getType(), is(expectedCustomer.getType()));
        }

        assertThat("products", actualData.getProducts(),
                hasSize(expectedData.getProducts().size()));

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

    @Step("Check that PIN code is set")
    public void assertThatPinCodeIsSet(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
    }

    @Step("Check that response is OK")
    public void assertThatRearranged(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
    }

    @Step("Check that is confirmed. Response body matches expected data.")
    public void assertThatIsConfirmed(Response<OrderData> resp, OrderData expectedData) {
        assertThatResponseIsOk(resp);
        OrderData actualData = resp.asJson();
        assertThat("orderId", actualData.getOrderId(), is(expectedData.getOrderId()));
        assertThat("fullDocId", actualData.getFullDocId(), is(expectedData.getFullDocId()));
        assertThat("status", actualData.getStatus(),
                is(SalesDocumentsConst.States.IN_PROGRESS.getApiVal()));
        assertThat("salesDocStatus", actualData.getSalesDocStatus(),
                is(SalesDocumentsConst.States.IN_PROGRESS.getApiVal()));
    }

    @Step("Check that check Quantity response is OK. Response body matches expected data")
    public void assertThatCheckQuantityIsOk(Response<ResOrderCheckQuantityData> resp,
            List<ReqOrderProductData> expectedProductDataList) {
        assertThatResponseIsOk(resp);
        ResOrderCheckQuantityData actualData = resp.asJson();
        assertThat("result", actualData.getResult(), is("OK"));
        assertThat("groupingId", actualData.getGroupingId(), is("ON_ORDER"));
        assertThat("product size", actualData.getProducts(),
                hasSize(expectedProductDataList.size()));
        for (int i = 0; i < actualData.getProducts().size(); i++) {
            ResOrderProductCheckQuantityData actualProduct = actualData.getProducts().get(i);
            ReqOrderProductData expectedProduct = expectedProductDataList.get(i);
            assertThat("Product " + (i + 1) + " lmCode", actualProduct.getLmCode(),
                    is(expectedProduct.getLmCode()));
            assertThat("Product " + (i + 1) + " quantity", actualProduct.getQuantity(),
                    is(expectedProduct.getQuantity()));
            assertThat("Product " + (i + 1) + " lineId", actualProduct.getLineId(),
                    not(emptyOrNullString()));
            assertThat("Product " + (i + 1) + " title", actualProduct.getTitle(),
                    not(emptyOrNullString()));
            assertThat("Product " + (i + 1) + " barCode", actualProduct.getBarCode(),
                    not(emptyOrNullString()));
        }
    }

    @Step("Check that order is cancelled")
    public OrderClient assertThatIsCancelled(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        JsonNode respData = resp.asJson();
        assertThat("status", respData.get("status").asText(),
                is("CONFIRMED"));
        return this;
    }

    @Step("Check that change status response has result is OK")
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
    @Step("Wait until order is confirmed")
    public OrderData waitUntilOrderHasStatusAndReturnOrderData(
            String orderId, String expectedStatus, boolean isVerify) throws Exception {
        int maxTimeoutInSeconds = 180;
        long currentTimeMillis = System.currentTimeMillis();
        Response<OrderData> r = null;
        while (System.currentTimeMillis() - currentTimeMillis < maxTimeoutInSeconds * 1000) {
            r = getOrder(orderId);
            if (r.isSuccessful() && r.asJson().getStatus()
                    .equals(expectedStatus)) {
                Log.info("waitUntilOrderIsConfirmed() has executed for " +
                        (System.currentTimeMillis() - currentTimeMillis) / 1000 + " seconds");
                return r.asJson();
            }
            Thread.sleep(3000);
        }
        if (isVerify) {
            assertThat(
                    "Could not wait for the order to be confirmed. Timeout=" + maxTimeoutInSeconds
                            + ". " +
                            "Response error:" + r.toString(),
                    r.isSuccessful());
            assertThat("Could not wait for the order: " + orderId + " to be confirmed. Timeout="
                            + maxTimeoutInSeconds + ". " +
                            "Status:" + r.asJson().getStatus(), r.asJson().getStatus(),
                    is(expectedStatus));
        }
        return null;
    }

    public OrderData waitUntilOrderHasStatusAndReturnOrderData(
            String orderId, String expectedStatus) throws Exception {
        return waitUntilOrderHasStatusAndReturnOrderData(orderId, expectedStatus, true);
    }

    @Step("Wait until Order can be cancelled")
    public OrderData waitUntilOrderCanBeCancelled(
            String orderId) throws Exception {
        int maxTimeoutInSeconds = 180;
        long currentTimeMillis = System.currentTimeMillis();
        Response<OrderData> r = null;
        while (System.currentTimeMillis() - currentTimeMillis < maxTimeoutInSeconds * 1000) {
            r = getOrder(orderId);
            String status = r.asJson().getStatus();
            if (r.isSuccessful() &&
                    (status.equals(SalesDocumentsConst.States.CONFIRMED.getApiVal()) ||
                            status.equals(
                                    SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getApiVal()))) {
                Log.info("waitUntilOrderIsConfirmed() has executed for " +
                        (System.currentTimeMillis() - currentTimeMillis) / 1000 + " seconds");
                return r.asJson();
            }
            Thread.sleep(3000);
        }
        assertThat("Could not wait for the order to be confirmed. Timeout=" + maxTimeoutInSeconds
                        + ". " +
                        "Response error:" + r.toString(),
                r.isSuccessful());
        return null;
    }

}
