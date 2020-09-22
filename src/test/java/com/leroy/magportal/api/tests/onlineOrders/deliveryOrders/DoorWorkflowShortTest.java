package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionResponse;

public class DoorWorkflowShortTest extends BaseMagPortalApiTest {

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


    @BeforeClass
    private void setUp() {
        List<BitrixSolutionResponse> bitrixSolutionResponses = bitrixHelper
                .createOnlineOrders(1, OnlineOrderTypeConst.DELIVERY_TO_DOOR, 3);
        currentOrderId = bitrixSolutionResponses.stream().findAny().get().getSolutionId();
        bitrixSolutionResponses.remove(bitrixSolutionResponses.stream()
                .filter(x -> x.getSolutionId().equals(currentOrderId)).findFirst().get());

        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.ALLOWED_FOR_PICKING, PaymentStatusEnum.HOLD);

        currentTaskId = pickingTaskClient.searchForPickingTasks(currentOrderId).asJson().getItems()
                .stream().findFirst().get().getTaskId();
    }

    @Test(description = "C23425626 Door Delivery: ALLOWED_FOR_PICKING -> PICKING_IN_PROGRESS")
    public void testStartPicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .startPicking(currentTaskId);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PICKING_IN_PROGRESS);
    }

    @Test(description = "C23425626 Door Delivery: PICKING_IN_PROGRESS -> PICKED", dependsOnMethods={"testStartPicking"})
    public void testCompletePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentTaskId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.PICKED_WAIT);
    }

    @Test(description = "C23425626 Door Delivery: ALLOWED_FOR_GIVEAWAY -> ON_SHIPMENT", dependsOnMethods={"testCompletePicking"})
    public void testShipped() {
        paymentHelper.makePaid(currentOrderId);
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.PICKED, PaymentStatusEnum.PAID);
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.GIVEN_AWAY);
    }

    @Test(description = "C23425626 Door Delivery: ON_SHIPMENT -> DELIVERED", dependsOnMethods={"testShipped"})
    public void testDeliver() {
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.SHIPPED, PaymentStatusEnum.PAID);
        Response<JsonNode> response = orderClient.deliver(currentOrderId, true);
        orderClient.assertWorkflowResult(response, currentOrderId, States.DELIVERED);
    }
}