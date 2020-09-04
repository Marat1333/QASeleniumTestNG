package com.leroy.magportal.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderProductData;
import com.leroy.magportal.api.constants.OrderReasonEnum;
import com.leroy.magportal.api.constants.OrderWorkflowEnum;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.constants.PaymentTypeEnum;
import com.leroy.magportal.api.data.onlineOrders.OrderFulfilmentToGivenAwayPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload.ProductDataPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload.WorkflowPayload;
import com.leroy.magportal.api.requests.order.OrderFulfilmentGivenAwayRequest;
import com.leroy.magportal.api.requests.order.OrderGetRequest;
import com.leroy.magportal.api.requests.order.OrderWorkflowRequest;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.List;
import ru.leroymerlin.qa.core.clients.base.Response;

public class OrderClient extends com.leroy.magmobile.api.clients.OrderClient {

    @Step("Get order with id = {orderId}")
    public Response<OrderData> getOrder(String orderId) {
        OrderGetRequest req = new OrderGetRequest();
        req.setOrderId(orderId);
        return execute(req, OrderData.class);
    }

    @Step("Cancel order with id = {orderId}")
    public Response<JsonNode> cancelOrder(String orderId) {
        return makeAction(orderId, OrderWorkflowEnum.CANCEL.getValue(), new OrderWorkflowPayload());
    }

    @Step("Edit order with id = {orderId}")//TODO: override rearrange
    public Response<JsonNode> editOrder(String orderId) {
        OrderData orderData = this.getOrder(orderId).asJson();
        if ((orderData.getPaymentType().equals(PaymentTypeEnum.CASH.getMashName()) || orderData
                .getPaymentType().equals(PaymentTypeEnum.CASH_OFFLINE.getMashName())) && !orderData
                .getPaymentStatus().equals(PaymentStatusEnum.PAID.toString())) {
            rearrange(orderData, new OrderProductData());
        } else {
            editPrePayment(orderId, false);
        }
        return makeAction(orderId, OrderWorkflowEnum.EDIT.getValue(), new OrderWorkflowPayload());
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

    private Response<JsonNode> editPrePayment(String orderId, Boolean isFull) {
        return makeAction(orderId, OrderWorkflowEnum.EDIT.getValue(),
                makeWorkflowPayload(orderId, isFull, true));
    }

    private Response<OrderFulfilmentToGivenAwayPayload> productsToGivenAway(String orderId) {
        OrderData orderData = this.getOrder(orderId).asJson();
        return execute(new OrderFulfilmentGivenAwayRequest()
                        .setFulfillmentTaskId(orderData.getFulfillmentTaskId())
                        .setUserLdap(userSessionData.getUserLdap()),
                OrderFulfilmentToGivenAwayPayload.class);
    }

    private Response<JsonNode> makeAction(String orderId, String action,
            OrderWorkflowPayload payload) {
        payload.setAction(action);
        return execute(new OrderWorkflowRequest()
                .setOrderId(orderId)
                .setUserLdap(userSessionData.getUserLdap())
                .jsonBody(payload), JsonNode.class);
    }

    private OrderWorkflowPayload makeWorkflowPayload(String orderId, Boolean isFull,
            Boolean isDeliver) {
        double count = 1;
        String reason = OrderReasonEnum.CLIENT.getValue();
        List<OrderProductData> orderData;

        OrderWorkflowPayload payload = new OrderWorkflowPayload();
        WorkflowPayload workflowPayload = new WorkflowPayload();
        List<ProductDataPayload> products = new ArrayList<>();

        if (isDeliver) {
            orderData = this.getOrder(orderId).asJson().getProducts();
        } else {
            orderData = this.productsToGivenAway(orderId).asJson().getGroups()
                    .stream().filter(x -> x.getGroupName().equals("TO_GIVEAWAY")).findFirst().get()
                    .getProducts();
        }

        for (OrderProductData productData : orderData) {
            if (isFull) {
                count = productData.getConfirmedQuantity();
                reason = "";
            }
            ProductDataPayload productDataPayload = new ProductDataPayload();
            productDataPayload.setLineId(productData.getLineId());
            productDataPayload.setQuantity(count);
            productDataPayload.setReason(reason);
            products.add(productDataPayload);
        }
        workflowPayload.setProducts(products);
        payload.setWorkflowPayload(workflowPayload);
        return payload;
    }
}
