package com.leroy.magportal.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderProductData;
import com.leroy.magportal.api.constants.OrderReasonEnum;
import com.leroy.magportal.api.constants.OrderWorkflowEnum;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload.ProductDataPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload.WorkflowPayload;
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


    private Response<JsonNode> makeAction(String orderId, String action,
            OrderWorkflowPayload payload) {
        payload.setAction(action);
        return execute(new OrderWorkflowRequest()
                .setOrderId(orderId)
                .setUserLdap(userSessionData.getUserLdap())
                .jsonBody(payload), JsonNode.class);
    }

    private OrderWorkflowPayload makeWorkflowPayload(String orderId, Boolean isFull) {
        double count = 1;
        String reason = OrderReasonEnum.CLIENT.getValue();

        OrderWorkflowPayload payload = new OrderWorkflowPayload();
        WorkflowPayload workflowPayload = new WorkflowPayload();
        List<ProductDataPayload> products = new ArrayList<>();
        OrderData orderData = this.getOrder(orderId).asJson();
        for (OrderProductData productData : orderData.getProducts()) {
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
