package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class DoorWorkflowFullTest extends BaseMagPortalApiTest {

    @Inject
    private BitrixHelper bitrixHelper;
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
        currentOrderType = OnlineOrderTypeConst.DELIVERY_TO_DOOR;
        currentOrderId = bitrixHelper.createOnlineOrder(currentOrderType).getSolutionId();

        currentTaskId = pickingTaskClient.searchForPickingTasks(currentOrderId).asJson().getItems()
                .stream().findFirst().get().getTaskId();
    }

    @Test(description = "C0")
    public void testStartPicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .startPicking(currentTaskId);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PICKING_IN_PROGRESS);
    }

    @Test(description = "C0", dependsOnMethods = {
            "testStartPicking"})
    public void testCompletePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentTaskId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PICKED_WAIT);
    }

    @Test(description = "C0", dependsOnMethods = {
            "testCompletePicking"})
    public void testShipped() {
        paymentHelper.makePaid(currentOrderId);
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.PICKED, PaymentStatusEnum.PAID);
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.SHIPPED);
    }

    @Test(description = "C0", dependsOnMethods = {
            "testShipped"})
    public void testDeliver() {
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.ON_DELIVERY,null);
        Response<JsonNode> response = orderClient.deliver(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.DELIVERED);
    }
}