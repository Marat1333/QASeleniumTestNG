package com.leroy.magportal.api.clients;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
import com.leroy.magportal.api.data.onlineOrders.OrderDeliveryRecalculatePayload;
import com.leroy.magportal.api.data.onlineOrders.OrderDeliveryRecalculateResponseData;
import com.leroy.magportal.api.data.onlineOrders.OrderFulfilmentToGivenAwayPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderProductDataPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderRearrangePayload;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload.WorkflowPayload;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.requests.order.OrderDeliveryRecalculateRequest;
import com.leroy.magportal.api.requests.order.OrderFulfilmentGivenAwayRequest;
import com.leroy.magportal.api.requests.order.OrderGetRequest;
import com.leroy.magportal.api.requests.order.OrderWorkflowRequest;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder.In;
import lombok.SneakyThrows;
import ru.leroymerlin.qa.core.clients.base.Response;

public class OrderClient extends com.leroy.magmobile.api.clients.OrderClient {

    @Inject
    private CatalogSearchClient catalogSearchClient;
    @Inject
    private PickingTaskClient pickingTaskClient;
    @Inject
    private PaymentHelper paymentHelper;

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
    public Response<JsonNode> rearrange(String orderId, Integer newProductsCount, Double newCount) {
        OrderRearrangeRequest req = new OrderRearrangeRequest();
        req.setShopId(getUserSessionData().getUserShopId());//TODO: move to default constructor
        req.setUserLdap(getUserSessionData().getUserLdap());
        req.setOrderId(orderId);
        OrderRearrangePayload orderRearrangePayload = makeRearrangePayload(orderId,
                newProductsCount, newCount);
        req.jsonBody(orderRearrangePayload);

        return execute(req, JsonNode.class);
    }

    @Step("Rearrange order: add specified product")
    public Response<JsonNode> rearrange(String orderId, String lmCode) {
        OrderRearrangeRequest req = new OrderRearrangeRequest();
        req.setShopId(getUserSessionData().getUserShopId());//TODO: move to default constructor
        req.setUserLdap(getUserSessionData().getUserLdap());
        req.setOrderId(orderId);
        OrderRearrangePayload orderRearrangePayload = makeRearrangePayloadForProduct(orderId,
                lmCode);
        req.jsonBody(orderRearrangePayload);

        return execute(req, JsonNode.class);
    }

    @Step("Edit for prepayment order with id = {orderId}")
    public Response<JsonNode> editPrePayment(String orderId, Double newCount) {
        return makeAction(orderId, OrderWorkflowEnum.EDIT.getValue(),
                makeEditPayload(orderId, newCount));
    }

    @Step("Edit order with id = {orderId}: Decreases ALL positions on 1 item if possible + adds Products for rearrange")
    public Response<JsonNode> editOrder(String orderId, Integer newProductsCount) {
        return this.editOrder(orderId, newProductsCount, null);
    }

    @Step("Edit order with id = {orderId}: Decreases ALL positions on 1 item if possible + adds Products for rearrange")
    public Response<JsonNode> editOrder(String orderId, Integer newProductsCount, Double newCount) {
        OrderData orderData = this.getOrder(orderId).asJson();
        if ((orderData.getPaymentType().equals(PaymentTypeEnum.CASH.getMashName()) || orderData
                .getPaymentType().equals(PaymentTypeEnum.CASH_OFFLINE.getMashName())) && !orderData
                .getPaymentStatus().equals(PaymentStatusEnum.PAID.toString())) {
            return rearrange(orderId, newProductsCount, newCount);
        } else {
            return editPrePayment(orderId, newCount);
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

    @Step("Recalculate Delivery price for order with id = {orderId}")
    public Response<OrderDeliveryRecalculateResponseData> deliveryRecalculate(String orderId, Integer productCount, Double newCount) {
        OrderDeliveryRecalculateRequest req = new OrderDeliveryRecalculateRequest();
        req.setOrderId(orderId);
        req.jsonBody(makeDeliveryRecalculationPayload(orderId, productCount, newCount));

        return execute(req, OrderDeliveryRecalculateResponseData.class);
    }

    @Step("Moves NEW order to specified status")
    public void moveNewOrderToStatus(String orderId, States status) {
        OrderData orderData = this.getOrder(orderId).asJson();
        if (status.equals(States.ALLOWED_FOR_PICKING)) {
            this.waitUntilOrderGetStatus(orderId, States.ALLOWED_FOR_PICKING, null);
            return;
        }
        States pickedState = States.PICKED;
        if (orderData.getPaymentType().equalsIgnoreCase(PaymentTypeEnum.SBERBANK.getMashName())) {
            pickedState = States.PICKED_WAIT;
        }

        pickingTaskClient.startAllPickings(orderId);
        this.waitUntilOrderGetStatus(orderId, States.PICKING_IN_PROGRESS, null);

        switch (status) {
            case PARTIALLY_PICKED:
                pickingTaskClient.completeAllPickings(orderId, false);
                break;
            case PICKED:
                pickingTaskClient.completeAllPickings(orderId, true);
                this.waitUntilOrderGetStatus(orderId, pickedState, null);
                paymentHelper.makePaid(orderId);
                break;
            case PARTIALLY_GIVEN_AWAY:
                pickingTaskClient.completeAllPickings(orderId, true);
                this.waitUntilOrderGetStatus(orderId, pickedState, null);
                paymentHelper.makePaid(orderId);
                this.waitAndReturnProductsReadyToGiveaway(orderId);
                this.giveAway(orderId, false);
                break;
            case GIVEN_AWAY:
                pickingTaskClient.completeAllPickings(orderId, true);
                this.waitUntilOrderGetStatus(orderId, pickedState, null);
                paymentHelper.makePaid(orderId);
                this.waitAndReturnProductsReadyToGiveaway(orderId);
                this.giveAway(orderId, true);
                break;
            default:
                break;
        }
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
                        .getProducts();

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

    private OrderWorkflowPayload makeEditPayload(String orderId, Double newCount) {
        OrderWorkflowPayload payload = makeWorkflowPayload(orderId, true, true);
        if (newCount != null) {
            for (OrderProductDataPayload productData : payload.getWorkflowPayload().getProducts()) {
                productData.setQuantity(newCount);
                productData.setReason(null);
            }
        } else {
            for (OrderProductDataPayload productData : payload.getWorkflowPayload().getProducts()) {
                productData.setQuantity(makeNewCount(productData.getQuantity(), false));
                productData.setReason(null);
            }
        }

        return payload;
    }

    private OrderRearrangePayload makeRearrangePayload(String orderId, Integer newProductsCount,
            Double newCount) {
        List<OrderProductDataPayload> orderProducts = new ArrayList<>();
        OrderRearrangePayload payload = new OrderRearrangePayload();
        OrderData orderData = this.getOrder(orderId).asJson();
        Double count = orderData.getProducts().stream().findAny().get().getConfirmedQuantity();
        if (count == null || count == 0) {
            count = orderData.getProducts().stream().findAny().get().getQuantity();
        }
        payload.setFulfillmentTaskId(orderData.getFulfillmentTaskId());
        payload.setFulfillmentVersion(orderData.getFulfillmentVersion());
        payload.setPaymentTaskId(orderData.getPaymentTaskId());
        payload.setPaymentVersion(orderData.getPaymentVersion());
        payload.setSolutionVersion(orderData.getSolutionVersion());

        List<ProductItemData> newProducts = catalogSearchClient
                .getProductsForShop(newProductsCount, orderData.getShopId());
        for (ProductItemData productData : newProducts) {
            OrderProductDataPayload orderProductDataPayload = new OrderProductDataPayload();
            orderProductDataPayload.setLmCode(productData.getLmCode());
            orderProductDataPayload.setPrice(productData.getPrice());
            orderProductDataPayload.setType("PRODUCT");
            orderProductDataPayload.setQuantity(count);

            orderProducts.add(orderProductDataPayload);
        }

        if (newCount != null) {
            for (OrderProductDataPayload productData : orderProducts) {
                productData.setQuantity(newCount);
            }

            OrderWorkflowPayload orderWorkflowPayload = makeEditPayload(orderId, newCount);
            for (OrderProductDataPayload productData : orderWorkflowPayload.getWorkflowPayload()
                    .getProducts()) {
                productData.setType("PRODUCT");
                orderProducts.add(productData);
            }
        }

        payload.setProducts(orderProducts);
        return payload;
    }

    private OrderRearrangePayload makeRearrangePayloadForProduct(String orderId, String lmCode) {
        List<OrderProductDataPayload> orderProducts = new ArrayList<>();
        OrderRearrangePayload payload = this.makeRearrangePayload(orderId, 0, null);

        ProductItemData product = catalogSearchClient.getProductByLmCode(lmCode);

        OrderProductDataPayload orderProductDataPayload = new OrderProductDataPayload();
        orderProductDataPayload.setLmCode(product.getLmCode());
        orderProductDataPayload.setPrice(product.getPrice());
        orderProductDataPayload.setType("PRODUCT");
        orderProductDataPayload.setQuantity(10.00);

        orderProducts.add(orderProductDataPayload);

        payload.setProducts(orderProducts);
        return payload;
    }

    private OrderDeliveryRecalculatePayload makeDeliveryRecalculationPayload(String orderId, Integer productCount, Double newCount) {
        OrderDeliveryRecalculatePayload payload = new OrderDeliveryRecalculatePayload();
        List<OrderProductDataPayload> products = new ArrayList<>();
        OrderData orderData = this.getOrder(orderId).asJson();
        int i = 0;

        for (OrderProductData product : orderData.getProducts()) {
            OrderProductDataPayload productData = new OrderProductDataPayload();
            productData.setLmCode(product.getLmCode());
            productData.setQuantity(newCount);
            if (i < productCount) {
                products.add(productData);
            }
            i++;
        }
        payload.setProducts(products);

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
        if (expectedStatus.equals(States.CANCELLED) && status
                .equalsIgnoreCase(States.CANCELLATION_IN_PROGRESS.getApiVal())) {
            status = States.CANCELLED.getApiVal();
        }
        assertThat(
                "Order Status match FAILED. \nActual: " + status + "\nExpected: " + expectedStatus
                        .getApiVal(),
                status.equalsIgnoreCase(expectedStatus.getApiVal()));
    }

    @Step("Order Rearrange results verification")
    public void assertRearrangeResult(Response<?> response, String orderId, Double expectedCount,
            Integer productsCount) {
        assertThat("Request to change Order Status has Failed.", response, successful());
        Response<OrderData> order = this.getOrder(orderId);
        assertThat("Get Order request failed.", order, successful());
        List<OrderProductData> products = order.asJson().getProducts();
        assertThat("INVALID Products count in Order. \nActual: " + products.size() + "\nExpected: "
                        + productsCount,
                products, hasSize(productsCount));
        for (OrderProductData product : products) {
            assertThat(
                    "INVALID count of product in Order. \nActual: " + product.getConfirmedQuantity()
                            + "\nExpected: " + expectedCount + "\nLmCode: " + product.getLmCode(),
                    product.getConfirmedQuantity().equals(expectedCount));
        }
    }

    @Step("Order Edit results verification")
    public void assertEditResult(Response<?> response, String orderId, Double expectedCount) {
        assertThat("Request to change Order Status has Failed.", response, successful());
        Response<OrderData> order = this.getOrder(orderId);
        assertThat("Get Order request failed.", order, successful());
        List<OrderProductData> products = order.asJson().getProducts();
        for (OrderProductData product : products) {
            assertThat(
                    "INVALID count of product in Order. \nActual: " + product.getConfirmedQuantity()
                            + "\nExpected: " + expectedCount + "\nLmCode: " + product.getLmCode(),
                    product.getConfirmedQuantity().equals(expectedCount));
        }
    }
}
