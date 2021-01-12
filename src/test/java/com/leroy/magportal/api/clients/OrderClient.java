package com.leroy.magportal.api.clients;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderProductData;
import com.leroy.magmobile.api.data.sales.orders.ResOrderCheckQuantityData;
import com.leroy.magmobile.api.requests.order.OrderRearrangeRequest;
import com.leroy.magportal.api.constants.DeliveryServiceTypeEnum;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.constants.OrderChannelEnum;
import com.leroy.magportal.api.constants.OrderReasonEnum;
import com.leroy.magportal.api.constants.OrderWorkflowEnum;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.constants.PaymentTypeEnum;
import com.leroy.magportal.api.data.onlineOrders.CheckQuantityData;
import com.leroy.magportal.api.data.onlineOrders.DeliveryCustomerData;
import com.leroy.magportal.api.data.onlineOrders.DeliveryData;
import com.leroy.magportal.api.data.onlineOrders.DeliveryUpdatePayload;
import com.leroy.magportal.api.data.onlineOrders.OnlineOrderData;
import com.leroy.magportal.api.data.onlineOrders.OrderDeliveryRecalculateResponseData;
import com.leroy.magportal.api.data.onlineOrders.OrderFulfilmentToGivenAwayPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderProductDataPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderRearrangePayload;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload;
import com.leroy.magportal.api.data.onlineOrders.OrderWorkflowPayload.WorkflowPayload;
import com.leroy.magportal.api.data.onlineOrders.ShipToData;
import com.leroy.magportal.api.data.timeslot.AppointmentData;
import com.leroy.magportal.api.data.timeslot.AppointmentPayload;
import com.leroy.magportal.api.data.timeslot.AppointmentResponseData;
import com.leroy.magportal.api.data.timeslot.TimeslotData;
import com.leroy.magportal.api.data.timeslot.TimeslotPayload;
import com.leroy.magportal.api.data.timeslot.TimeslotResponseData;
import com.leroy.magportal.api.data.timeslot.TimeslotUpdatePayload;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.requests.order.CheckQuantityRequest;
import com.leroy.magportal.api.requests.order.DeliveryUpdateRequest;
import com.leroy.magportal.api.requests.order.OrderDeliveryRecalculateRequest;
import com.leroy.magportal.api.requests.order.OrderFulfilmentGivenAwayRequest;
import com.leroy.magportal.api.requests.order.OrderGetAdditionalProductsInfo;
import com.leroy.magportal.api.requests.order.OrderGetRequest;
import com.leroy.magportal.api.requests.order.OrderWorkflowRequest;
import com.leroy.magportal.api.requests.timeslot.AppointmentsRequest;
import com.leroy.magportal.api.requests.timeslot.ChangeDateRequest;
import com.leroy.magportal.api.requests.timeslot.TimeslotRequest;
import io.qameta.allure.Step;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.testng.asserts.SoftAssert;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;

public class OrderClient extends com.leroy.magmobile.api.clients.OrderClient {

    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private PickingTaskClient pickingTaskClient;
    @Inject
    private PaymentHelper paymentHelper;

    private final int waitTimeoutInSeconds = 300;

    @Step("Get order with id = {orderId} with response verification")
    public Response<OnlineOrderData> getOnlineOrder(String orderId) {
        return this.getOnlineOrder(orderId, true);
    }

    @SneakyThrows
    @Step("Get order with id = {orderId}")
    public Response<OnlineOrderData> getOnlineOrder(String orderId, boolean isVerify) {
        OrderGetRequest req = new OrderGetRequest();
        req.setOrderId(orderId);
        long currentTimeMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() - currentTimeMillis < waitTimeoutInSeconds * 1000) {
            Response<OnlineOrderData> response = execute(req, OnlineOrderData.class);
            if (!isVerify || response.isSuccessful() && response.asJson().getOrderId() != null) {
                return response;
            }
            Thread.sleep(3000);
        }
        return null;
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

    @Step("Edit order with id = {orderId}: Decreases ALL positions on 1 item if possible + adds Products for rearrange")
    public Response<JsonNode> editOrder(String orderId, Integer newProductsCount) {
        return this.editOrder(orderId, newProductsCount, null);
    }

    @Step("Edit order with id = {orderId}: Decreases ALL positions on 1 item if possible + adds Products for rearrange")
    public Response<JsonNode> editOrder(String orderId, Integer newProductsCount, Double newCount) {
        OnlineOrderData orderData = this.getOnlineOrder(orderId).asJson();
        if ((orderData.getPaymentType().equals(PaymentTypeEnum.CASH.getMashName()) || orderData
                .getPaymentType().equals(PaymentTypeEnum.CASH_OFFLINE.getMashName())) && !orderData
                .getPaymentStatus().equals(PaymentStatusEnum.PAID.toString())) {
            return rearrange(orderId, newProductsCount, newCount);
        } else {
            return editPrePaymentOrder(orderId, newCount);
        }
    }

    @Step("Edit for prepayment order with id = {orderId}")
    public Response<JsonNode> editPrePaymentOrder(String orderId, Double newCount) {
        return makeAction(orderId, OrderWorkflowEnum.EDIT.getValue(),
                makeEditPayload(orderId, newCount));
    }

    @Step("GetAvailable stock for List<lmCodes>")
    public Response<ResOrderCheckQuantityData> checkQuantity(List<String> lmCodes, Double newCount,
            LocalDateTime gateAwayDate) {
        CheckQuantityRequest req = new CheckQuantityRequest();
        CheckQuantityData quantityData = new CheckQuantityData();
        List<BaseProductOrderData> orderDataList = new ArrayList<>();
        for (String lmCode : lmCodes) {
            BaseProductOrderData productOrderData = new OrderProductData();
            productOrderData.setQuantity(newCount);
            productOrderData.setLmCode(lmCode);
            orderDataList.add(productOrderData);
        }
        quantityData.setProducts(orderDataList);
        quantityData.setDateOfGiveAway(gateAwayDate);
        req.setShopId(getUserSessionData().getUserShopId());
        req.jsonBody(quantityData);
        return execute(req, ResOrderCheckQuantityData.class);
    }

    @Step("Edit order with id = {orderId}: Decreases ALL positions on 1 item if possible + adds Products for rearrange")
    public Response<JsonNode> editDeliveryOrder(String orderId, Double deliveryTotalPrice,
            Double liftPrice) {
        OrderWorkflowPayload payload = makeEditPayload(orderId, null);
        payload.getWorkflowPayload().setDeliveryLiftPrice(liftPrice);
        payload.getWorkflowPayload().setDeliveryTotalPrice(deliveryTotalPrice);
        return makeAction(orderId, OrderWorkflowEnum.EDIT.getValue(), payload);
    }

    @Step("Recalculate Delivery price for order with id = {orderId}")
    public Response<OrderDeliveryRecalculateResponseData> deliveryRecalculate(String orderId,
            Integer productCount, Double newCount) {
        OrderDeliveryRecalculateRequest req = new OrderDeliveryRecalculateRequest();
        req.setOrderId(orderId);
        req.jsonBody(makeDeliveryRecalculationPayload(orderId, productCount, newCount));

        return execute(req, OrderDeliveryRecalculateResponseData.class);
    }

    @Step("GiveAway products for order with id = {orderId}")
    public Response<JsonNode> giveAway(String orderId, Boolean isFull) {
        if(this.waitAndReturnProductsReadyToGiveaway(orderId) != null) {
            return makeAction(orderId, OrderWorkflowEnum.GIVEAWAY.getValue(),
                    makeWorkflowPayload(orderId, isFull, false));
        } else {
            return null;
        }
    }

    @Step("Deliver products for order with id = {orderId}")
    public Response<JsonNode> deliver(String orderId, Boolean isFull) {
        return makeAction(orderId, OrderWorkflowEnum.DELIVER.getValue(),
                makeWorkflowPayload(orderId, isFull, true));
    }

    @Step("Returns available Timeslots for order with id = {orderId}")
    public Response<TimeslotResponseData> getTimeslots(String orderId) {
        OnlineOrderData orderData = this.getOnlineOrder(orderId).asJson();
        TimeslotPayload payload = makeTimeslotPayload(orderData);
        TimeslotRequest req = new TimeslotRequest();
        req.jsonBody(payload);
        return execute(req, TimeslotResponseData.class);
    }

    @Step("Updates Timeslots for order with id = {orderId}")
    public Response<JsonNode> updateTimeslot(String orderId, TimeslotData timeslotData) {
        OnlineOrderData orderData = this.getOnlineOrder(orderId).asJson();
        TimeslotPayload timeslotPayload = makeTimeslotPayload(orderData);
        TimeslotUpdatePayload payload = new TimeslotUpdatePayload();
        payload.setFulfillmentTaskId(orderData.getFulfillmentTaskId());
        payload.setLmCodes(timeslotPayload.getLmCodes());
        payload.setStores(timeslotPayload.getStores());
        payload.setAvailableDate(timeslotData.getAvailableDate());

        ChangeDateRequest req = new ChangeDateRequest();
        req.setUserLdap(getUserSessionData().getUserLdap());
        req.jsonBody(payload);
        return execute(req, JsonNode.class);
    }

    @Step("Returns available Timeslots for DELIVERY order with id = {orderId}")
    public Response<AppointmentResponseData> getAppointments(String orderId) {
        OnlineOrderData orderData = this.getOnlineOrder(orderId).asJson();
        TimeslotPayload payload = makeTimeslotPayload(orderData);
        AppointmentsRequest req = new AppointmentsRequest();
        req.jsonBody(payload);
        return execute(req, AppointmentResponseData.class);
    }

    @Step("Updates Appointment data for order with id = {orderId}")
    public Response<JsonNode> updateAppointment(String orderId, AppointmentData appointmentData) {
        return updateDeliveryDataAndAppointment(orderId, null, appointmentData);
    }

    @Step("Updates Delivery data for order with id = {orderId}")
    public Response<JsonNode> updateDeliveryData(String orderId, DeliveryData newDeliveryData) {
        return updateDeliveryDataAndAppointment(orderId, newDeliveryData, null);
    }

    @Step("Updates Delivery data and Appointment for order with id = {orderId}")
    public Response<JsonNode> updateDeliveryDataAndAppointment(String orderId,
            DeliveryData newDeliveryData, AppointmentData appointmentData) {
        OnlineOrderData orderData = this.getOnlineOrder(orderId).asJson();
        DeliveryUpdatePayload payload = makeDeliveryUpdatePayload(orderData, appointmentData,
                newDeliveryData);

        DeliveryUpdateRequest req = new DeliveryUpdateRequest();
        req.setUserLdap(getUserSessionData().getUserLdap());
        req.setTaskId(orderData.getDeliveryData().getId());
        req.jsonBody(payload);
        return execute(req, JsonNode.class);
    }

    @Step("Get Products Additional Info for {lmCodes} for user's shopId")
    public Response<?> getProductsAdditionalInfo(
            List<String> lmCodes) {
        return getProductsAdditionalInfo(lmCodes, getUserSessionData().getUserShopId());
    }

    @Step("Get Products Additional Info for {lmCodes} for {shopId}")
    public Response<?> getProductsAdditionalInfo(List<String> lmCodes,
            String shopId) {
        OrderGetAdditionalProductsInfo req = new OrderGetAdditionalProductsInfo();
        req.setLmCodes(String.join(",", lmCodes));
        req.setShopId(shopId);
        return execute(req, Object.class);
    }

    @Step("Moves NEW order to specified status")
    public void moveNewOrderToStatus(String orderId, States status) {
        OnlineOrderData orderData = this.getOnlineOrder(orderId).asJson();
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
        long currentTimeMillis = System.currentTimeMillis();
        Response<OnlineOrderData> r = null;
        while (System.currentTimeMillis() - currentTimeMillis < waitTimeoutInSeconds * 1000) {
            r = this.getOnlineOrder(orderId, false);
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

        assertThat("Could not wait for the order. Timeout=" + waitTimeoutInSeconds + ". " +
                        "Response error:" + r.asJson().toString(),
                r.isSuccessful());
        assertThat("Could not wait for the order: " + orderId + ". Timeout="
                        + waitTimeoutInSeconds + ". " + "Status:" + r.asJson().getStatus(),
                r.asJson().getStatus(),
                is(expectedOrderStatus.getApiVal()));
        if (expectedPaymentStatus != null) {
            assertThat(
                    "Could not wait for the order: " + orderId + ". Timeout=" + waitTimeoutInSeconds
                            + ". " +
                            "Payment Status:" + r.asJson().getPaymentStatus(),
                    r.asJson().getPaymentStatus(),
                    is(expectedPaymentStatus.toString()));
        }
    }

    @SneakyThrows
    @Step("Wait and return products are ready to TO_GIVEAWAY")
    public List<OrderProductData> waitAndReturnProductsReadyToGiveaway(String orderId) {
        long currentTimeMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() - currentTimeMillis < waitTimeoutInSeconds * 1000) {
            Response<OrderFulfilmentToGivenAwayPayload> response = this
                    .productsToGivenAway(orderId);
            if (response.isSuccessful()) {
                List<OrderProductData> products = response.asJson().getGroups().stream()
                        .filter(x -> x.getGroupName().equals("TO_GIVEAWAY")).findFirst().get()
                        .getProducts();
                if (products.size() > 0) {
                    return products;
                }
            }
            Thread.sleep(3000);
        }
        return null;
    }

    private Response<OrderFulfilmentToGivenAwayPayload> productsToGivenAway(String orderId) {
        OnlineOrderData orderData = this.getOnlineOrder(orderId).asJson();
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
            orderData = this.getOnlineOrder(orderId).asJson().getProducts();
        } else {
            orderData = waitAndReturnProductsReadyToGiveaway(orderId);
        }

        for (OrderProductData productData : orderData) {
            if (isFull == null) {
                count = 0.0;
            } else if (isFull) {
                reason = "";
                count = productData.getConfirmedQuantity();
                if (count == 0.0) {
                    count = productData.getCreatedQuantity();
                }
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
        OnlineOrderData orderData = this.getOnlineOrder(orderId).asJson();
        Double count = orderData.getProducts().stream().findAny().get().getConfirmedQuantity();
        if (count == null || count == 0) {
            count = orderData.getProducts().stream().findAny().get().getCreatedQuantity();
        }
        payload.setFulfillmentTaskId(orderData.getFulfillmentTaskId());
        payload.setFulfillmentVersion(orderData.getFulfillmentVersion());
        payload.setPaymentTaskId(orderData.getPaymentTaskId());
        payload.setPaymentVersion(orderData.getPaymentVersion());
        payload.setSolutionVersion(orderData.getSolutionVersion());

        List<ProductItemData> newProducts = searchProductHelper
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

        ProductItemData product = searchProductHelper.getProductByLmCode(lmCode);

        OrderProductDataPayload orderProductDataPayload = new OrderProductDataPayload();
        orderProductDataPayload.setLmCode(product.getLmCode());
        orderProductDataPayload.setPrice(product.getPrice());
        orderProductDataPayload.setType("PRODUCT");
        orderProductDataPayload.setQuantity(10.00);

        orderProducts.add(orderProductDataPayload);

        payload.setProducts(orderProducts);
        return payload;
    }

    private WorkflowPayload makeDeliveryRecalculationPayload(String orderId,
            Integer productCount, Double newCount) {
        OnlineOrderData orderData = this.getOnlineOrder(orderId).asJson();
        WorkflowPayload productsPayload = new WorkflowPayload();
        List<OrderProductDataPayload> products = new ArrayList<>();
        int i = 0;

        for (OrderProductData product : orderData.getProducts()) {
            OrderProductDataPayload productPayload = new OrderProductDataPayload();
            productPayload.setLmCode(product.getLmCode());
            productPayload.setQuantity(newCount);
            if (i < productCount) {
                products.add(productPayload);
            }
            i++;
        }
        productsPayload.setProducts(products);
        return productsPayload;
    }

    private AppointmentPayload makeTimeslotPayload(OnlineOrderData orderData) {
        List<String> lmCodes = new ArrayList<>();
        List<String> stores = new ArrayList<>();
        AppointmentPayload payload = new AppointmentPayload();

        if (orderData.getDeliveryData() != null) {
            DeliveryData deliveryData = orderData.getDeliveryData();
            payload.setDeliveryId(deliveryData.getId());
            payload.setReferenceStoreId(deliveryData.getReferenceStoreId());
            stores.add(deliveryData.getShipFromShopId());
        } else {
            stores.add(orderData.getShopId());
        }

        for (OrderProductData product : orderData.getProducts()) {
            lmCodes.add(product.getLmCode());
        }

        payload.setLmCodes(lmCodes);
        payload.setDate(ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));

        payload.setStores(stores);
        return payload;
    }

    private DeliveryUpdatePayload makeDeliveryUpdatePayload(OnlineOrderData orderData,
            AppointmentData appointmentData, DeliveryData newDeliveryData) {
        AppointmentPayload appointmentPayload = makeTimeslotPayload(orderData);
        appointmentPayload.setDeliveryId(null);
        DeliveryUpdatePayload payload = new DeliveryUpdatePayload();

        payload.setOrderId(orderData.getOrderId());
        payload.setFulfillmentTaskId(orderData.getFulfillmentTaskId());
        payload.setLmCodes(appointmentPayload.getLmCodes());
        payload.setStores(appointmentPayload.getStores());
        payload.setDate(appointmentPayload.getDate());
        payload.setReferenceStoreId(appointmentPayload.getReferenceStoreId());
        payload.setLongitude(orderData.getDeliveryData().getShipTo().getGpsX());
        payload.setLatitude(orderData.getDeliveryData().getShipTo().getGpsY());

        if (newDeliveryData != null) {
            if (newDeliveryData.getReceiver() != null) {
                DeliveryCustomerData customerData = new DeliveryCustomerData();
                customerData.setFullName(newDeliveryData.getReceiver().getFullName());
                customerData.setPhone(newDeliveryData.getReceiver().getPhone());

                payload.setReceiver(customerData);
            }

            if (newDeliveryData.getShipTo() != null) {
                ShipToData shipToData = new ShipToData();
                shipToData.setIntercom(newDeliveryData.getShipTo().getIntercom());
                shipToData.setEntrance(newDeliveryData.getShipTo().getEntrance());

                payload.setShipTo(shipToData);
            }
        }

        if (appointmentData != null) {
            payload.setAppointmentStart(appointmentData.getStart());
            payload.setAppointmentEnd(appointmentData.getEnd());
        }

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
        Response<OnlineOrderData> order = this.getOnlineOrder(orderId);
        String status = order.asJson().getStatus();
        if (expectedStatus.equals(States.CANCELLED) && status
                .equalsIgnoreCase(States.CANCELLATION_IN_PROGRESS.getApiVal())) {
            status = States.CANCELLED.getApiVal();
        }
        assertThat(
                "Order Status match FAILED. \nActual: " + status + "\nExpected: " + expectedStatus
                        .getApiVal(),
                status, equalToIgnoringCase(expectedStatus.getApiVal()));
    }

    @Step("Order Rearrange results verification")
    public void assertRearrangeResult(Response<?> response, String orderId, Double expectedCount,
            Integer productsCount) {
        assertThat("Request to Update Order has Failed.", response, successful());
        Response<OnlineOrderData> order = this.getOnlineOrder(orderId);
        List<OrderProductData> products = order.asJson().getProducts();
        assertThat("INVALID Products count in Order. \nActual: " + products.size() + "\nExpected: "
                        + productsCount,
                products, hasSize(productsCount));
        for (OrderProductData product : products) {
            assertThat(
                    "INVALID count of product in Order. \nActual: " + product.getConfirmedQuantity()
                            + "\nExpected: " + expectedCount + "\nLmCode: " + product.getLmCode(),
                    product.getConfirmedQuantity(), equalTo(expectedCount));
        }
    }

    @Step("Order Edit results verification")
    public void assertEditResult(Response<?> response, String orderId, Double expectedCount) {
        assertThat("Request to Update Order has Failed.", response, successful());
        Response<OnlineOrderData> order = this.getOnlineOrder(orderId);
        List<OrderProductData> products = order.asJson().getProducts();
        for (OrderProductData product : products) {
            assertThat(
                    "INVALID count of product in Order. \nActual: " + product.getConfirmedQuantity()
                            + "\nExpected: " + expectedCount + "\nLmCode: " + product.getLmCode(),
                    product.getConfirmedQuantity(), equalTo(expectedCount));
        }
    }

    @Step("Order Delivery Recalculate results verification")
    public void assertDeliveryRecalculateResult(
            Response<OrderDeliveryRecalculateResponseData> response, String orderId) {
        assertThat("Request to Recalculate Delivery Cost has Failed.", response, successful());
        OrderDeliveryRecalculateResponseData newDeliveryData = response.asJson();
        Response<OnlineOrderData> orderResp = this.getOnlineOrder(orderId);
        DeliveryData orderDeliveryData = orderResp.asJson().getDeliveryData();
        assertThat("Delivery Lift Price INVALID.", newDeliveryData.getDeliveryLiftPrice(),
                lessThanOrEqualTo(orderDeliveryData.getLiftupServicePrice()));
        assertThat("Delivery Price INVALID.", newDeliveryData.getDeliveryTotalPrice(),
                lessThanOrEqualTo(orderDeliveryData.getTotalServicePrice()));
    }

    @Step("Order Delivery Edit results verification")
    public void assertDeliveryUpdateResult(Response<?> response, String orderId,
            Double expectedTotalDeliveryPrice, Double expectedLiftPrice) {
        assertThat("Request to Edit with Delivery recalculate has Failed.", response, successful());
        Response<OnlineOrderData> orderResp = this.getOnlineOrder(orderId);
        DeliveryData orderDeliveryData = orderResp.asJson().getDeliveryData();
        assertThat("Delivery Lift Price was NOT updated.",
                orderDeliveryData.getLiftupServicePrice(),
                equalTo(expectedLiftPrice));
        assertThat("Delivery Price was NOT updated.", orderDeliveryData.getTotalServicePrice(),
                equalTo(expectedTotalDeliveryPrice));
    }

    @Step("Storage Location Verification")
    public void assertLocationChanged(String orderId, int locationsCount) {
        OnlineOrderData orderData = this.getOnlineOrder(orderId).asJson();
        assertThat("Storage locations count in Order is invalid.",
                orderData.getStorageLocations().size(), lessThanOrEqualTo(locationsCount));
    }

    @Step("GET Order Verification")
    public void assertGetOrderResult(Response<OnlineOrderData> response,
            OnlineOrderTypeData currentOrderType) {
        assertThat("Request to GET Order has Failed.", response, successful());
        OnlineOrderData orderData = response.asJson();
        SoftAssert softAssert = new SoftAssert();
//        PaymentTypeEnum.values().
        if (currentOrderType == null) {
            softAssert.assertEquals(orderData.getChannel(), OrderChannelEnum.OFFLINE.getValue(),
                    "Channel is invalid.");
            softAssert.assertEquals(orderData.getPaymentType(),
                    PaymentTypeEnum.CASH_OFFLINE.getMashName(), "Payment Type is invalid.");
        } else {
            softAssert.assertEquals(orderData.getChannel(), OrderChannelEnum.ONLINE.getValue(),
                    "Channel is invalid.");
            softAssert.assertEquals(orderData.getPaymentType(),
                    PaymentTypeEnum.getMashNameByName(currentOrderType.paymentType),
                    "Payment Type is invalid.");
            if (!currentOrderType.getDeliveryType().getService()
                    .equals(DeliveryServiceTypeEnum.PICKUP.getService())) {
                softAssert.assertTrue(orderData.getDelivery(), "Delivery is invalid.");
                softAssert.assertNotNull(orderData.getDeliveryData(), "Delivery Data is empty.");
            } else {
                softAssert.assertFalse(orderData.getDelivery(), "Delivery is invalid.");
            }
        }
        softAssert
                .assertTrue(orderData.getProducts().size() > 0, "There are No Products in order.");
        softAssert.assertTrue(Strings.isNotNullAndNotEmpty(orderData.getFulfillmentTaskId()),
                "FulfillmentTaskId is empty.");
        softAssert
                .assertTrue(orderData.getFulfillmentVersion() > 0, "FulfillmentVersion is empty.");
        softAssert.assertTrue(Strings.isNotNullAndNotEmpty(orderData.getOrderId()),
                "OrderId is empty.");
        softAssert.assertTrue(Strings.isNotNullAndNotEmpty(orderData.getPaymentTaskId()),
                "PaymentTaskId is empty.");
        softAssert.assertTrue(Strings.isNotNullAndNotEmpty(orderData.getPaymentStatus()),
                "PaymentStatus is empty.");
        softAssert.assertTrue(orderData.getPaymentVersion() > 0, "PaymentVersion is empty.");
        softAssert.assertTrue(orderData.getSolutionVersion() > 0, "SolutionVersion is empty.");

        softAssert.assertAll();
    }
}
