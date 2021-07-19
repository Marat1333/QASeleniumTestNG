package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.AllureId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.customerorders.enums.PaymentStatus;

public class EntranceWorkflowShortTest extends BaseMagPortalApiTest {

    @Inject
    private OnlineOrderHelper onlineOrderHelper;
    @Inject
    private PaymentHelper paymentHelper;
    @Inject
    private OrderClient orderClient;
    @Inject
    private PickingTaskClient pickingTaskClient;

    private String currentOrderId;
    private String currentTaskId;


    @BeforeClass
    private void setUp() {
        OnlineOrderTypeData currentOrderType = OnlineOrderTypeConst.DELIVERY_TO_ENTRANCE;
        currentOrderId = onlineOrderHelper.createOnlineOrderCardPayment(currentOrderType)
                .getSolutionId();

        currentTaskId = pickingTaskClient.searchForPickingTasks(currentOrderId).asJson().getItems()
                .stream().findFirst().get().getTaskId();
    }

    @Test(description = "C23425627 Entrance Delivery: ALLOWED_FOR_PICKING -> PICKING_IN_PROGRESS")
    @AllureId("1779")
    public void testStartPicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .startPicking(currentTaskId);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PICKING_IN_PROGRESS);
    }

    @Test(description = "C23425627 Entrance Delivery: PICKING_IN_PROGRESS -> PICKED", dependsOnMethods = {
            "testStartPicking"})
    @AllureId("1779")
    public void testCompletePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentTaskId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PICKED_WAIT);
    }

    @Test(description = "C23425627 Entrance Delivery: ALLOWED_FOR_GIVEAWAY -> ON_SHIPMENT", dependsOnMethods = {
            "testCompletePicking"})
    @AllureId("1779")
    public void testShipped() {
        paymentHelper.makePaid(currentOrderId);
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.PICKED, PaymentStatus.PAID);
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.SHIPPED);
    }

    @Test(description = "C23425627 Entrance Delivery: ON_SHIPMENT -> DELIVERED", dependsOnMethods = {
            "testShipped"})
    @AllureId("1779")
    public void testDeliver() {
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.ON_DELIVERY, null);
        Response<JsonNode> response = orderClient.deliver(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.DELIVERED);
    }
}