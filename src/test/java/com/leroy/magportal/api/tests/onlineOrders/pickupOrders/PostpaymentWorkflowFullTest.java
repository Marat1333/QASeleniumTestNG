package com.leroy.magportal.api.tests.onlineOrders.pickupOrders;

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

public class PostpaymentWorkflowFullTest extends BaseMagPortalApiTest {

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
        currentOrderType = OnlineOrderTypeConst.PICKUP_POSTPAYMENT;
        currentOrderId = onlineOrderHelper.createOnlineOrder(currentOrderType).getSolutionId();

        currentTaskId = pickingTaskClient.searchForPickingTasks(currentOrderId).asJson().getItems()
                .stream().findFirst().get().getTaskId();
    }

    @Test(description = "C23438378 ALLOWED_FOR_PICKING -> PICKING_IN_PROCESS", priority = 1)
    @AllureId("16078")
    public void testStartPicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .startPicking(currentTaskId);
        assertResult(response, States.PICKING_IN_PROGRESS, PickingStatus.PICKING_IN_PROGRESS);
    }

    @Test(description = "C23438379 PICKING_IN_PROGRESS -> PAUSE_PICKING (pause-picking)", dependsOnMethods = {
            "testStartPicking"}, priority = 2)
    @AllureId("16079")
    public void testPausePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .pausePicking(currentTaskId);
        assertResult(response, States.PICKING_IN_PROGRESS, PickingStatus.PAUSE_PICKING);
    }

    @Test(description = "C23438380 PAUSE_PICKING -> PICKING_IN_PROCESS (unpause-picking)", dependsOnMethods = {
            "testPausePicking"}, priority = 3)
    @AllureId("16080")
    public void testResumePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .resumePicking(currentTaskId);
        assertResult(response, States.PICKING_IN_PROGRESS, PickingStatus.PICKING_IN_PROGRESS);
    }

    @Test(description = "C23438381 PICKING_IN_PROGRESS -> PARTIALLY_PICKED", dependsOnMethods = {
            "testStartPicking"}, priority = 4)
    @AllureId("16081")
    public void testPartiallyCompletePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentTaskId, false);
        assertResult(response, States.PARTIALLY_PICKED, PickingStatus.PARTIALLY_PICKED);
    }

    @Test(description = "C23438382 PARTIALLY_PICKED: NEW Storage Location", priority = 5)
    @AllureId("16082")
    public void testNewStorageLocation() {
        currentLocationsCount = 3;
        Response<PickingTaskData> response = pickingTaskClient
                .locatePicking(currentTaskId, currentLocationsCount);
        assertResult(response, States.PARTIALLY_PICKED, PickingStatus.PARTIALLY_PICKED);
        assertLocationChanged();
    }

    @Test(description = "C23438383 PARTIALLY_PICKED: Updated Storage Location", priority = 6)
    @AllureId("16083")
    public void testUpdateStorageLocation() {
        currentLocationsCount = 1;
        Response<PickingTaskData> response = pickingTaskClient
                .locatePicking(currentTaskId, currentLocationsCount);
        assertResult(response, States.PARTIALLY_PICKED, PickingStatus.PARTIALLY_PICKED);
        assertLocationChanged();
    }

    @Test(description = "C23438384 PARTIALLY_PICKED -> PICKED", priority = 7)
    @AllureId("16084")
    public void testPicked() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentTaskId, true);
        assertResult(response, States.PICKED, PickingStatus.PICKED);
    }

    @Test(description = "C23438385 PICKED: UPDATE Storage Location", priority = 8)
    @AllureId("16085")
    public void testUpdateStorageLocationPicked() {
        currentLocationsCount = 5;
        Response<PickingTaskData> response = pickingTaskClient
                .locatePicking(currentTaskId, currentLocationsCount);
        assertResult(response, States.PICKED, PickingStatus.PICKED);
        assertLocationChanged();
    }

    @Test(description = "C23438386 PICKED -> PARTIALLY_GIVEN_AWAY", dependsOnMethods = {
            "testPicked"}, priority = 9)
    @AllureId("16086")
    public void testPartiallyGivenAway() {
        paymentHelper.makePaid(currentOrderId);
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, false);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PARTIALLY_GIVEN_AWAY);
    }

    @Test(description = "C23438387 PARTIALLY_GIVEN_AWAY -> GIVEN_AWAY", dependsOnMethods = {
            "testPicked"}, priority = 10)
    @AllureId("16087")
    public void testGivenAway() {
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.GIVEN_AWAY);
    }

    @Test(description = "C23438401 GET Order", priority = 11)
    @AllureId("16088")
    public void testGetOrder() {
        Response<OnlineOrderData> response = orderClient.getOnlineOrder(currentOrderId);
        orderClient.assertGetOrderResult(response, OnlineOrderTypeConst.PICKUP_POSTPAYMENT);
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