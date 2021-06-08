package com.leroy.magmobile.api.clients;

import static com.leroy.core.matchers.Matchers.isNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderProductData;
import com.leroy.magmobile.api.data.sales.orders.ReqOrderData;
import com.leroy.magmobile.api.data.sales.orders.ReqOrderProductData;
import com.leroy.magmobile.api.requests.order.OrderChangeStatusRequest;
import com.leroy.magmobile.api.requests.order.OrderConfirmRequest;
import com.leroy.magmobile.api.requests.order.OrderPost;
import com.leroy.magmobile.api.requests.order.OrderPutRequest;
import com.leroy.magmobile.api.requests.order.OrderRearrangeRequest;
import com.leroy.magmobile.api.requests.order.OrderSetPinCodeRequest;
import io.qameta.allure.Step;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PaoClient extends BasePaoClient {

    @Step("Create order")
    public Response<OrderData> createOrder(ReqOrderData reqOrderData) {
        OrderPost orderPost = new OrderPost();
        orderPost.bearerAuthHeader(getUserSessionData().getAccessToken());
        orderPost.jsonBody(reqOrderData);
        orderPost.setShopId(getUserSessionData().getUserShopId());
        orderPost.setUserLdap(getUserSessionData().getUserLdap());
        return execute(orderPost, OrderData.class);
    }

    @Step("Confirm order with id = {orderId}")
    public Response<OrderData> confirmOrder(String orderId, OrderData putOrderData) {
        OrderConfirmRequest req = new OrderConfirmRequest();
        req.setShopId(getUserSessionData().getUserShopId());
        req.setUserLdap(getUserSessionData().getUserLdap());
        req.setOrderId(orderId);
        req.jsonBody(putOrderData);
        return execute(req, OrderData.class);
    }

    @Step("Set {pinCode} PIN code for order with id = {orderId}")
    public Response<JsonNode> setPinCode(String orderId, String pinCode) {
        OrderSetPinCodeRequest req = new OrderSetPinCodeRequest();
        req.setOrderId(orderId);
        Map<String, String> body = new HashMap<>();
        body.put("pinCode", pinCode);
        req.jsonBody(body);
        return execute(req, JsonNode.class);
    }

    @Step("Update Order")
    public Response<OrderData> updateDraftOrder(OrderData orderData) {
        OrderPutRequest req = new OrderPutRequest();
        req.setShopId(getUserSessionData().getUserShopId());
        req.setUserLdap(getUserSessionData().getUserLdap());
        req.jsonBody(orderData);
        return execute(req, OrderData.class);
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
        return execute(req, JsonNode.class);
    }

    @Step("Make status DELETED for order with id = {orderId}")
    public Response<JsonNode> deleteDraftOrder(String orderId) {
        OrderChangeStatusRequest req = new OrderChangeStatusRequest();
        req.setOrderId(orderId);
        req.setUserLdap(getUserSessionData().getUserLdap());
        Map<String, String> body = new HashMap<>();
        body.put("status", SalesDocumentsConst.States.DELETED.getApiVal());
        req.jsonBody(body);
        return execute(req, JsonNode.class);
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
//        assertThat("delivery", data.getDelivery(), not(nullValue()));//TODO uncomment when fixed

        return data;
    }

    @Step("Check that Response body contains products: {expectedProducts}")
    public PaoClient assertThatResponseContainsAddedProducts(
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

    @Step("Check that PIN code is set")
    public void assertThatPinCodeIsSet(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
    }

    @Step("Check that is confirmed. Response body matches expected data.")
    public void assertThatIsConfirmed(Response<OrderData> resp, OrderData expectedData) {
        assertThatResponseIsOk(resp);
        OrderData actualData = resp.asJson();
        assertThat("orderId", actualData.getOrderId(), is(expectedData.getOrderId()));
        assertThat("fullDocId", actualData.getFullDocId(), is(expectedData.getOrderId()));
        assertThat("status", actualData.getStatus(),
                is(SalesDocumentsConst.States.IN_PROGRESS.getApiVal()));
        assertThat("salesDocStatus", actualData.getSalesDocStatus(),
                is(SalesDocumentsConst.States.IN_PROGRESS.getApiVal()));
    }

}
