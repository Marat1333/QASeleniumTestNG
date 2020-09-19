package com.leroy.magportal.api.tests.offlineOrders;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class WorkflowShortTest extends BaseMagPortalApiTest {

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


    @BeforeClass
    private void setUp() {
        OrderData orderData = paoHelper.createConfirmedOrder(paoHelper.makeCartProducts(3), true);
        currentOrderId = orderData.getOrderId();

        currentTaskId = pickingTaskClient.searchForPickingTasks(currentOrderId).asJson().getItems()
                .stream().findFirst().get().getTaskId();
    }

    @Test(description = "C0 OFFLINE: Start Picking the Order")
    public void testStartPicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .startPicking(currentTaskId);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PICKING_IN_PROGRESS);
    }

    @Test(description = "C1 OFFLINE: Complete Picking the Order", dependsOnMethods = {
            "testStartPicking"})
    public void testCompletePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentTaskId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PICKED);
    }

    @Test(description = "C2 OFFLINE: Give away the Order", dependsOnMethods = {
            "testCompletePicking"})
    public void testGiveAway() throws Exception {
        paymentHelper.makePaid(currentOrderId);
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.PICKED, PaymentStatusEnum.PAID);
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.GIVEN_AWAY);
    }
}