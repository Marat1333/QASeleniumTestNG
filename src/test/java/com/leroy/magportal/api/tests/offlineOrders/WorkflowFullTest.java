package com.leroy.magportal.api.tests.offlineOrders;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.PickingStatus;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.data.onlineOrders.OnlineOrderData;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;
import io.qameta.allure.AllureId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class WorkflowFullTest extends BaseMagPortalApiTest {

    @Inject
    private PAOHelper paoHelper;
    @Inject
    private PaymentHelper paymentHelper;
    @Inject
    private OrderClient orderClient;
    @Inject
    private PickingTaskClient pickingTaskClient;

    private String currentOrderId;
    private String currentTaskId;
    private int currentLocationsCount;


    @BeforeClass
    private void setUp() {
        OrderData orderData = paoHelper.createConfirmedPickupOrder(paoHelper.makeCartProducts(3), true);
        currentOrderId = orderData.getOrderId();

        currentTaskId = pickingTaskClient.searchForPickingTasks(currentOrderId).asJson().getItems()
                .stream().findFirst().get().getTaskId();
    }

    @Test(description = "C23438388 ALLOWED_FOR_PICKING -> PICKING_IN_PROCESS", priority = 1)
    @AllureId("16187")
    public void testStartPicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .startPicking(currentTaskId);
        assertResult(response, States.PICKING_IN_PROGRESS, PickingStatus.PICKING_IN_PROGRESS);
    }

    @Test(description = "C23438389 PICKING_IN_PROGRESS -> PAUSE_PICKING (pause-picking)", dependsOnMethods = {
            "testStartPicking"}, priority = 2)
    @AllureId("16188")
    public void testPausePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .pausePicking(currentTaskId);
        assertResult(response, States.PICKING_IN_PROGRESS, PickingStatus.PAUSE_PICKING);
    }

    @Test(description = "C23438390 PAUSE_PICKING -> PICKING_IN_PROCESS (unpause-picking)", dependsOnMethods = {
            "testPausePicking"}, priority = 3)
    @AllureId("16189")
    public void testResumePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .resumePicking(currentTaskId);
        assertResult(response, States.PICKING_IN_PROGRESS, PickingStatus.PICKING_IN_PROGRESS);
    }

    @Test(description = "C23438391 PICKING_IN_PROGRESS -> PARTIALLY_PICKED", dependsOnMethods = {
            "testStartPicking"}, priority = 4)
    @AllureId("16190")
    public void testPartiallyCompletePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentTaskId, false);
        assertResult(response, States.PARTIALLY_PICKED, PickingStatus.PARTIALLY_PICKED);
    }

    @Test(description = "C23438392 PARTIALLY_PICKED: NEW Storage Location", priority = 5)
    @AllureId("16191")
    public void testNewStorageLocation() {
        currentLocationsCount = 3;
        Response<PickingTaskData> response = pickingTaskClient
                .locatePicking(currentTaskId, currentLocationsCount);
        assertResult(response, States.PARTIALLY_PICKED, PickingStatus.PARTIALLY_PICKED);
        assertLocationChanged();
    }

    @Test(description = "C23438393 PARTIALLY_PICKED: Updated Storage Location", priority = 6)
    @AllureId("16192")
    public void testUpdateStorageLocation() {
        currentLocationsCount = 1;
        Response<PickingTaskData> response = pickingTaskClient
                .locatePicking(currentTaskId, currentLocationsCount);
        assertResult(response, States.PARTIALLY_PICKED, PickingStatus.PARTIALLY_PICKED);
        assertLocationChanged();
    }

    @Test(description = "C23438394 PARTIALLY_PICKED -> PICKED", priority = 7)
    @AllureId("16193")
    public void testPicked() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentTaskId, true);
        assertResult(response, States.PICKED, PickingStatus.PICKED);
    }

    @Test(description = "C23438395 PICKED: UPDATE Storage Location", priority = 8)
    @AllureId("16194")
    public void testUpdateStorageLocationPicked() {
        currentLocationsCount = 5;
        Response<PickingTaskData> response = pickingTaskClient
                .locatePicking(currentTaskId, currentLocationsCount);
        assertResult(response, States.PICKED, PickingStatus.PICKED);
        assertLocationChanged();
    }

    @Test(description = "C23438396 PICKED -> PARTIALLY_GIVEN_AWAY", dependsOnMethods = {
            "testPicked"}, priority = 9)
    @AllureId("16195")
    public void testPartiallyGivenAway() {
        paymentHelper.makePaid(currentOrderId);
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, false);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PARTIALLY_GIVEN_AWAY);
    }

    @Test(description = "C23438397 PARTIALLY_GIVEN_AWAY -> GIVEN_AWAY", dependsOnMethods = {
            "testPicked"}, priority = 10)
    @AllureId("16196")
    public void testGivenAway() {
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.GIVEN_AWAY);
    }

    @Test(description = "C23438403 GET Order", priority = 15)
    @AllureId("16197")
    public void testGetOrder() {
        Response<OnlineOrderData> response = orderClient.getOnlineOrder(currentOrderId);
        orderClient.assertGetOrderResult(response, null);
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