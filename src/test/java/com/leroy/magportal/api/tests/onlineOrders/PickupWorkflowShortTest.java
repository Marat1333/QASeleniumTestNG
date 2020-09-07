package com.leroy.magportal.api.tests.onlineOrders;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionResponse;

public class PickupWorkflowShortTest extends BaseMagPortalApiTest {

    @Inject
    private BitrixHelper bitrixHelper;
    @Inject
    private PaymentHelper paymentHelper;
    @Inject
    private OrderClient orderClient;
    @Inject
    private PickingTaskClient pickingTaskClient;

    private String currentOrderId;


    @BeforeClass
    private void setUp() throws Exception {
        List<BitrixSolutionResponse> bitrixSolutionResponses = bitrixHelper
                .createOnlineOrders(1, OnlineOrderTypeConst.PICKUP_POSTPAYMENT, 3);
        currentOrderId = bitrixSolutionResponses.stream().findAny().get().getSolutionId();
        bitrixSolutionResponses.remove(bitrixSolutionResponses.stream()
                .filter(x -> x.getSolutionId().equals(currentOrderId)).findFirst().get());

        orderClient.waitUntilOrderHasStatusAndReturnOrderData(currentOrderId,
                SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getApiVal());
    }
    
    @Test(description = "C3225834 PICKUP_POSTPAYMENT: Start Picking the Order", priority = 1)
    public void testStartPicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .startPicking(currentOrderId);
        assertThat("Failed to change Order Status", response, successful());
        Response<OrderData> order = orderClient.getOrder(currentOrderId);
        assertThat("Payment update failed",
                order.asJson().getStatus().equals(States.PICKING_IN_PROGRESS.getApiVal()));
    }

    @Test(description = "C3225834 PICKUP_POSTPAYMENT: Complete Picking the Order", priority = 2)
    public void testCompletePicking() {
        Response<PickingTaskData> response = pickingTaskClient
                .completePicking(currentOrderId, true);
        assertThat("Failed to change Order Status", response, successful());
        Response<OrderData> order = orderClient.getOrder(currentOrderId);
        assertThat("Payment update failed",
                order.asJson().getStatus().equals(States.PICKED.getApiVal()));
    }

    @Test(description = "C3225834 PICKUP_POSTPAYMENT: Give away the Order", priority = 3)
    public void testGiveAway() throws Exception {
        paymentHelper.makePaid(currentOrderId);
        orderClient.waitUntilOrderHasStatusAndReturnOrderData(currentOrderId,
                States.ALLOWED_FOR_GIVEAWAY.getApiVal());
        Response<JsonNode> response = orderClient.giveAway(currentOrderId, true);
        assertThat("Failed to change Order Status", response, successful());
        assertThat("Failed to change Order Status",
                response.asJson().toString().equals("status:" + States.GIVEN_AWAY.getApiVal()));
        Response<OrderData> order = orderClient.getOrder(currentOrderId);
        assertThat("Payment update failed",
                order.asJson().getStatus().equals(States.GIVEN_AWAY.getApiVal()));
    }
}