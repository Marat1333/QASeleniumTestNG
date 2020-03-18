package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderProductData;
import com.leroy.magmobile.api.data.sales.orders.PostOrderData;
import com.leroy.magmobile.api.data.sales.orders.PostOrderProductData;
import com.leroy.magmobile.api.requests.order.OrderConfirmRequest;
import com.leroy.magmobile.api.requests.order.OrderGet;
import com.leroy.magmobile.api.requests.order.OrderPost;
import com.leroy.magmobile.api.requests.order.OrderWorkflowPut;
import org.json.simple.JSONObject;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

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

    public Response<OrderData> createOrder(PostOrderData postOrderData) {
        OrderPost orderPost = new OrderPost();
        orderPost.bearerAuthHeader(sessionData.getAccessToken());
        orderPost.jsonBody(postOrderData);
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

        assertThat("products", data.getProducts(), hasSize(greaterThan(0)));
        assertThat("customers", data.getCustomers(), not(nullValue()));

        return data;
    }

    public OrderClient assertThatResponseContainsAddedProducts(
            Response<OrderData> resp, List<PostOrderProductData> expectedProducts) {
        assertThatResponseIsOk(resp);
        OrderData actualData = resp.asJson();
        for (int i = 0; i < actualData.getProducts().size(); i++) {
            OrderProductData actualProduct = actualData.getProducts().get(i);
            PostOrderProductData expectedProduct = expectedProducts.get(i);
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

    public OrderClient assertThatGetResponseMatches(Response<OrderData> resp, OrderData expectedData) {
        assertThatResponseIsOk(resp);
        OrderData actualData = resp.asJson();
        assertThat("orderId", actualData.getOrderId(), is(expectedData.getOrderId()));
        assertThat("shopId", actualData.getShopId(), is(expectedData.getShopId()));
        assertThat("createdBy", actualData.getCreatedBy(), is(sessionData.getUserLdap()));
        assertThat("fulfillmentTaskId", actualData.getFulfillmentTaskId(),
                is(expectedData.getFulfillmentTaskId()));
        assertThat("fulfillmentVersion", actualData.getFulfillmentVersion(),
                is(expectedData.getFulfillmentVersion()));
        assertThat("paymentTaskId", actualData.getPaymentTaskId(),
                is(expectedData.getPaymentTaskId()));
        assertThat("paymentVersion", actualData.getPaymentVersion(),
                is(expectedData.getPaymentVersion()));
        assertThat("status", actualData.getStatus(),
                is(expectedData.getStatus()));

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
        return this;
    }

    public OrderClient assertThatIsCancelled(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        JsonNode respData = resp.asJson();
        assertThat("status", respData.get("status").asText(), is("CONFIRMED"));
        return this;
    }


}
