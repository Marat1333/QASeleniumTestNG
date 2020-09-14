package com.leroy.magportal.api.clients;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderProductData;
import com.leroy.magmobile.api.requests.order.OrderRearrangeRequest;
import com.leroy.magportal.api.constants.OrderReasonEnum;
import com.leroy.magportal.api.constants.OrderWorkflowEnum;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.constants.PaymentTypeEnum;
import com.leroy.magportal.api.data.onlineOrders.OrderFulfilmentToGivenAwayPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderProductDataPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderRearrangePayload;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload.WorkflowPayload;
import com.leroy.magportal.api.requests.order.OrderFulfilmentGivenAwayRequest;
import com.leroy.magportal.api.requests.order.OrderGetRequest;
import com.leroy.magportal.api.requests.order.OrderWorkflowRequest;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import ru.leroymerlin.qa.core.clients.base.Response;

public class OrderClient extends com.leroy.magmobile.api.clients.OrderClient {

    @Inject
    private CatalogSearchClient catalogSearchClient;

    @Override
    @Step("Get order with id = {orderId}")
    public Response<OrderData> getOrder(String orderId) {
        OrderGetRequest req = new OrderGetRequest();
        req.setOrderId(orderId);
        return execute(req, OrderData.class);
    }

    @Override
    @Step("Cancel order with id = {orderId}")
    public Response<JsonNode> cancelOrder(String orderId) {
        return makeAction(orderId, OrderWorkflowEnum.CANCEL.getValue(), new OrderWorkflowPayload());
    }

    @Step("Rearrange order")
    public Response<JsonNode> rearrange(String orderId, Integer newProductsCount) {
        OrderRearrangeRequest req = new OrderRearrangeRequest();
        OrderRearrangePayload orderRearrangePayload = makeRearrangePayload(orderId,
                newProductsCount, true);
        req.jsonBody(orderRearrangePayload);

        return execute(req, JsonNode.class);
    }

    @Step("Edit for prepayment order with id = {orderId}")
    public Response<JsonNode> editPrePayment(String orderId) {
        return makeAction(orderId, OrderWorkflowEnum.EDIT.getValue(),
                makeEditPayload(orderId));
    }

    @Step("Edit order with id = {orderId}: Decreases ALL positions on 1 item if possible + adds Products for rearrange")
    public Response<JsonNode> editOrder(String orderId, Integer newProductsCount) {
        OrderData orderData = this.getOrder(orderId).asJson();
        if ((orderData.getPaymentType().equals(PaymentTypeEnum.CASH.getMashName()) || orderData
                .getPaymentType().equals(PaymentTypeEnum.CASH_OFFLINE.getMashName())) && !orderData
                .getPaymentStatus().equals(PaymentStatusEnum.PAID.toString())) {
            return rearrange(orderId, newProductsCount);
        } else {
            return editPrePayment(orderId);
        }
    }

    @Step("GiveAway products for order with id = {orderId}")
    public Response<JsonNode> giveAway(String orderId, Boolean isFull) {
        return makeAction(orderId, OrderWorkflowEnum.GIVEAWAY.getValue(),
                makeWorkflowPayload(orderId, isFull, false));
    }

    @Step("Deliver products for order with id = {orderId}")
    public Response<JsonNode> deliver(String orderId, Boolean isFull) {
        return makeAction(orderId, OrderWorkflowEnum.DELIVER.getValue(),
                makeWorkflowPayload(orderId, isFull, true));
    }

    @SneakyThrows
    @Step("Wait until order comes to statuses. USE null for payment ignore")
    public void waitUntilOrderGetStatus(
            String orderId, States expectedOrderStatus, PaymentStatusEnum expectedPaymentStatus) {
        int maxTimeoutInSeconds = 180;
        long currentTimeMillis = System.currentTimeMillis();
        Response<OrderData> r = null;
        while (System.currentTimeMillis() - currentTimeMillis < maxTimeoutInSeconds * 1000) {
            r = this.getOrder(orderId);
            if (r.isSuccessful() && r.asJson().getStatus()
                    .equals(expectedOrderStatus.getApiVal())) {
                String paymentStatus = r.asJson().getPaymentStatus();
                if (expectedPaymentStatus == null) {
                    Log.info("waitUntilOrderGetStatus() has executed for " +
                            (System.currentTimeMillis() - currentTimeMillis) / 1000 + " seconds");
                    break;
                } else if (paymentStatus.equalsIgnoreCase(expectedPaymentStatus.toString())) {
                    Log.info("waitUntilOrderGetStatus() has executed for " +
                            (System.currentTimeMillis() - currentTimeMillis) / 1000 + " seconds");
                    break;
                }
            }
            Thread.sleep(3000);
        }

        assertThat("Could not wait for the order. Timeout=" + maxTimeoutInSeconds + ". " +
                        "Response error:" + r.asJson().toString(),
                r.isSuccessful());
        assertThat("Could not wait for the order: " + orderId + ". Timeout="
                        + maxTimeoutInSeconds + ". " + "Status:" + r.asJson().getStatus(),
                r.asJson().getStatus(),
                is(expectedOrderStatus.getApiVal()));
        if (expectedPaymentStatus != null) {
            assertThat(
                    "Could not wait for the order: " + orderId + ". Timeout=" + maxTimeoutInSeconds
                            + ". " +
                            "Payment Status:" + r.asJson().getPaymentStatus(),
                    r.asJson().getPaymentStatus(),
                    is(expectedPaymentStatus.toString()));
        }
    }

    @SneakyThrows
    @Step("Wait and return products are ready to TO_GIVEAWAY")
    public List<OrderProductData> waitAndReturnProductsReadyToGiveaway(String orderId) {
        int maxTimeoutInSeconds = 180;
        long currentTimeMillis = System.currentTimeMillis();
        Response<OrderFulfilmentToGivenAwayPayload> response;
        List<OrderProductData> products = null;
        while (System.currentTimeMillis() - currentTimeMillis < maxTimeoutInSeconds * 1000) {
            response = this.productsToGivenAway(orderId);
            if (response.isSuccessful()) {
                products = response.asJson().getGroups().stream()
                        .filter(x -> x.getGroupName().equals("TO_GIVEAWAY")).findFirst().get()
                        .getProducts();;
                if (products.size() > 0) {
                    return products;
                }

            }
            Thread.sleep(3000);
        }
        return products;
    }

    private Response<OrderFulfilmentToGivenAwayPayload> productsToGivenAway(String orderId) {
        OrderData orderData = this.getOrder(orderId).asJson();
        return execute(new OrderFulfilmentGivenAwayRequest()
                        .setFulfillmentTaskId(orderData.getFulfillmentTaskId())
                        .setUserLdap(getUserSessionData().getUserLdap()),
                OrderFulfilmentToGivenAwayPayload.class);
    }

    private Response<JsonNode> makeAction(String orderId, String action,
            OrderWorkflowPayload payload) {
        payload.setAction(action);
        return execute(new OrderWorkflowRequest()
                .setOrderId(orderId)
                .setUserLdap(getUserSessionData().getUserLdap())
                .jsonBody(payload), JsonNode.class);
    }

    private OrderWorkflowPayload makeWorkflowPayload(String orderId, Boolean isFull,
            Boolean isDeliver) {
        double count = 1;
        String reason = OrderReasonEnum.CLIENT.getValue();
        List<OrderProductData> orderData;

        OrderWorkflowPayload payload = new OrderWorkflowPayload();
        WorkflowPayload workflowPayload = new WorkflowPayload();
        List<OrderProductDataPayload> products = new ArrayList<>();

        if (isDeliver) {
            orderData = this.getOrder(orderId).asJson().getProducts();
        } else {
            orderData = waitAndReturnProductsReadyToGiveaway(orderId);
        }

        for (OrderProductData productData : orderData) {
            if (isFull) {
                count = productData.getConfirmedQuantity();
                reason = "";
            }
            OrderProductDataPayload productDataPayload = new OrderProductDataPayload();
            productDataPayload.setLineId(productData.getLineId());
            productDataPayload.setQuantity(count);
            productDataPayload.setReason(reason);
            products.add(productDataPayload);
        }
        workflowPayload.setProducts(products);
        payload.setWorkflowPayload(workflowPayload);
        return payload;
    }

    private OrderWorkflowPayload makeEditPayload(String orderId) {
        OrderWorkflowPayload payload = makeWorkflowPayload(orderId, true, true);
        for (OrderProductDataPayload productData : payload.getWorkflowPayload().getProducts()) {
            productData.setQuantity(makeNewCount(productData.getQuantity(), false));
            productData.setReason(null);
        }
        return payload;
    }

    private OrderRearrangePayload makeRearrangePayload(String orderId, Integer newProductsCount,
            Boolean isOldUpdated) {
        List<OrderProductDataPayload> orderProducts = new ArrayList<>();
        OrderRearrangePayload payload = new OrderRearrangePayload();
        OrderData orderData = this.getOrder(orderId).asJson();
        payload.setFulfillmentTaskId(orderData.getFulfillmentTaskId());
        payload.setFulfillmentVersion(orderData.getFulfillmentVersion());
        payload.setPaymentTaskId(orderData.getPaymentTaskId());
        payload.setPaymentVersion(orderData.getPaymentVersion());
        payload.setSolutionVersion(orderData.getSolutionVersion());

        if (isOldUpdated) {
            OrderWorkflowPayload orderWorkflowPayload = makeEditPayload(orderId);
            for (OrderProductDataPayload productData : orderWorkflowPayload.getWorkflowPayload()
                    .getProducts()) {
                productData.setType("PRODUCT");
                orderProducts.add(productData);
            }
        }

        List<ProductItemData> newProducts = catalogSearchClient
                .getProductsForShop(newProductsCount, orderData.getShopId());
        for (ProductItemData productData : newProducts) {
            OrderProductDataPayload orderProductDataPayload = new OrderProductDataPayload();
            orderProductDataPayload.setLmCode(productData.getLmCode());
            orderProductDataPayload.setPrice(productData.getPrice());
            orderProductDataPayload.setType("PRODUCT");
            orderProductDataPayload.setQuantity(10.00);

            orderProducts.add(orderProductDataPayload);
        }

        payload.setProducts(orderProducts);
        return payload;
    }

    private Double makeNewCount(Double count, Boolean isDimensional) {
        double decreaser = 1;
        if (isDimensional) {
            decreaser += 0.11;
        }
        double newCount = count - decreaser;
        if (newCount >= 0) {
            return newCount;
        } else {
            return count;
        }
    }

    ////VERIFICATION
    @Step("Order Status verification")
    public void assertWorkflowResult(Response<?> response, String orderId, States expectedStatus) {
        assertThat("Request to change Order Status has Failed.", response, successful());
        Response<OrderData> order = this.getOrder(orderId);
        assertThat("Get Order request failed.", order, successful());
        String status = order.asJson().getStatus();
        assertThat(
                "Order Status match FAILED. \nActual: " + status + "\nExpected: " + expectedStatus
                        .getApiVal(),
                status.equalsIgnoreCase(expectedStatus.getApiVal()));
    }
}
