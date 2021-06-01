package com.leroy.magportal.api.tests.onlineOrders.pickupOrders;

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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.customerorders.enums.PaymentStatus;

public class PostpaymentWorkflowShortTest extends BaseMagPortalApiTest {

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
    private OnlineOrderTypeData currentOrderType;

    @BeforeClass
    private void setUp() {
        currentOrderType = OnlineOrderTypeConst.PICKUP_POSTPAYMENT;
        currentOrderId = onlineOrderHelper.createOnlineOrder(currentOrderType).getSolutionId();

        currentTaskId = pickingTaskClient.searchForPickingTasks(currentOrderId).asJson().getItems()
                .stream().findFirst().get().getTaskId();
    }

    @Test(description = "C23425594 PICKUP POSTPAYMENT: Start Picking the Order")
    public void testStartPicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .startPicking(currentTaskId);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PICKING_IN_PROGRESS);
    }

    @Test(description = "C23425594 PICKUP POSTPAYMENT: Complete Picking the Order", dependsOnMethods = {
            "testStartPicking"})
    public void testCompletePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentTaskId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PICKED);
    }

    @Test(description = "C23425594 PICKUP POSTPAYMENT: Give away the Order", dependsOnMethods = {
            "testCompletePicking"})
    public void testGiveAway() {
        paymentHelper.makePaid(currentOrderId);
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.PICKED, PaymentStatus.PAID);
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.GIVEN_AWAY);
    }
}