package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.PickingStatus;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.data.onlineOrders.OnlineOrderData;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;
import io.qameta.allure.AllureId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.customerorders.enums.PaymentStatus;

public class ExpressWorkflowFullTest extends BaseMagPortalApiTest {

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
    private int currentLocationsCount;
    private OnlineOrderTypeData currentOrderType;


    @BeforeClass
    private void setUp() {
        currentOrderType = OnlineOrderTypeConst.DELIVERY_EXPRESS;
        currentOrderId = onlineOrderHelper.createOnlineOrderCardPayment(currentOrderType)
                .getSolutionId();

        currentTaskId = pickingTaskClient.searchForPickingTasks(currentOrderId).asJson().getItems()
                .stream().findFirst().get().getTaskId();
    }

    @Test(description = "23438353 ALLOWED_FOR_PICKING -> PICKING_IN_PROCESS", priority = 1)
    @AllureId("16007")
    public void testStartPicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .startPicking(currentTaskId);
        assertResult(response, States.PICKING_IN_PROGRESS, PickingStatus.PICKING_IN_PROGRESS);
    }

    @Test(description = "23438354 PICKING_IN_PROGRESS -> PAUSE_PICKING (pause-picking)", dependsOnMethods = {
            "testStartPicking"}, priority = 2)
    @AllureId("16008")
    public void testPausePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .pausePicking(currentTaskId);
        assertResult(response, States.PICKING_IN_PROGRESS, PickingStatus.PAUSE_PICKING);
    }

    @Test(description = "23438355 PAUSE_PICKING -> PICKING_IN_PROCESS (unpause-picking)", dependsOnMethods = {
            "testPausePicking"}, priority = 3)
    @AllureId("16009")
    public void testResumePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .resumePicking(currentTaskId);
        assertResult(response, States.PICKING_IN_PROGRESS, PickingStatus.PICKING_IN_PROGRESS);
    }

    @Test(description = "23438356 PICKING_IN_PROGRESS -> PARTIALLY_PICKED", dependsOnMethods = {
            "testStartPicking"}, priority = 4)
    @AllureId("16010")
    public void testPartiallyCompletePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentTaskId, false);
        assertResult(response, States.PARTIALLY_PICKED, PickingStatus.PARTIALLY_PICKED);
    }

    @Test(description = "23438357 PARTIALLY_PICKED: NEW Storage Location", priority = 5)
    @AllureId("16011")
    public void testNewStorageLocation() {
        currentLocationsCount = 3;
        Response<PickingTaskData> response = pickingTaskClient
                .locatePicking(currentTaskId, currentLocationsCount);
        assertResult(response, States.PARTIALLY_PICKED, PickingStatus.PARTIALLY_PICKED);
        assertLocationChanged();
    }

    @Test(description = "23438358 PARTIALLY_PICKED: Updated Storage Location", priority = 6)
    @AllureId("16012")
    public void testUpdateStorageLocation() {
        currentLocationsCount = 1;
        Response<PickingTaskData> response = pickingTaskClient
                .locatePicking(currentTaskId, currentLocationsCount);
        assertResult(response, States.PARTIALLY_PICKED, PickingStatus.PARTIALLY_PICKED);
        assertLocationChanged();
    }

    @Test(description = "23438359 PARTIALLY_PICKED -> PICKED_WAIT", dependsOnMethods = {
            "testStartPicking"}, priority = 7)
    @AllureId("16013")
    public void testCompletePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentTaskId, true);
        assertResult(response, States.PICKED_WAIT, PickingStatus.PICKED);
    }

    @Test(description = "C23438360 PICKED_WAIT -> PICKED", dependsOnMethods = {
            "testCompletePicking"}, priority = 8)
    @AllureId("16014")
    public void testPicked() {
        paymentHelper.makePaid(currentOrderId);
        orderClient.waitUntilOrderGetStatus(currentOrderId, States.PICKED, PaymentStatus.PAID);
        Response<?> response = pickingTaskClient.getPickingTask(currentTaskId);//just for assert
        assertResult(response, States.PICKED, PickingStatus.PICKED);
    }

    @Test(description = "C23438361 PICKED: UPDATE Storage Location", priority = 9)
    @AllureId("16015")
    public void testUpdateStorageLocationPicked() {
        currentLocationsCount = 5;
        Response<PickingTaskData> response = pickingTaskClient
                .locatePicking(currentTaskId, currentLocationsCount);
        assertResult(response, States.PICKED, PickingStatus.PICKED);
        assertLocationChanged();
    }

    @Test(description = "C23438362 PICKED -> PARTIALLY_SHIPPED", dependsOnMethods = {
            "testPicked"}, priority = 10)
    @AllureId("16016")
    public void testPartiallyShipped() {
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, false);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PARTIALLY_SHIPPED);
    }

    @Test(description = "C23438363 PARTIALLY_SHIPPED -> SHIPPED", dependsOnMethods = {
            "testPicked"}, priority = 11)
    @AllureId("16017")
    public void testShipped() {
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.SHIPPED);
    }

    @Test(description = "C23438364 SHIPPED -> PARTIALLY_DELIVERED", dependsOnMethods = {
            "testShipped"}, priority = 12)
    @AllureId("16018")
    public void testPartiallyDeliver() {
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.ON_DELIVERY, PaymentStatus.PAID);
        Response<JsonNode> response = orderClient.deliver(currentOrderId, false);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PARTIALLY_DELIVERED);
    }

    @Test(description = "C23438365 SHIPPED -> NO DELIVERED", priority = 13)
    @AllureId("16019")
    public void testNothingDelivered() {
        setUp();
        orderClient.moveNewOrderToStatus(currentOrderId, States.GIVEN_AWAY);
        Response<JsonNode> response = orderClient.deliver(currentOrderId, null);
        orderClient.assertWorkflowResult(response, currentOrderId,
                States.CANCELLED_BY_CUSTOMER_ON_DELIVERY);
    }

    @Test(description = "C23438366 SHIPPED -> DELIVERED", priority = 14)
    @AllureId("16020")
    public void testDelivered() {
        setUp();
        orderClient.moveNewOrderToStatus(currentOrderId, States.GIVEN_AWAY);
        Response<JsonNode> response = orderClient.deliver(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.DELIVERED);
    }

    @Test(description = "C23438400 GET Order", priority = 15)
    @AllureId("16021")
    public void testGetOrder() {
        Response<OnlineOrderData> response = orderClient.getOnlineOrder(currentOrderId);
        orderClient.assertGetOrderResult(response, currentOrderType);
    }

    ////VERIFICATION
    public void assertResult(Response<?> response, States expectedOrderStatus,
                             PickingStatus expectedPickingStatus) {
        orderClient.assertWorkflowResult(response, currentOrderId, expectedOrderStatus);
        pickingTaskClient.assertWorkflowResult(response, currentTaskId, expectedPickingStatus);
    }

    @Step("Storage Location Verification")
    public void assertLocationChanged() {
        orderClient.assertLocationChanged(currentOrderId, currentLocationsCount);
        pickingTaskClient.assertLocationChanged(currentTaskId, currentLocationsCount);
    }
}