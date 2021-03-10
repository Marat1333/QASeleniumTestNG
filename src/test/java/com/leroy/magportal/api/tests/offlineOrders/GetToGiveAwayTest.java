package com.leroy.magportal.api.tests.offlineOrders;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.data.onlineOrders.OrderProductsToGivenAwayData;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import lombok.SneakyThrows;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class GetToGiveAwayTest extends BaseMagPortalApiTest {

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
        OrderData orderData = paoHelper
                .createConfirmedPickupOrder(paoHelper.makeCartProducts(3), true);
        currentOrderId = orderData.getOrderId();

        currentTaskId = pickingTaskClient.searchForPickingTasks(currentOrderId).asJson().getItems()
                .stream().findFirst().orElse(new PickingTaskData()).getTaskId();
    }

    @Test(description = "C23748034 GivenAway Products for Start Picking")
    public void testStartPickingGivenAway() {
        Response<PickingTaskData> resp = pickingTaskClient.startPicking(currentTaskId);
        isResponseOk(resp);
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.PICKING_IN_PROGRESS);
    }

    @SneakyThrows
    @Test(description = "C23748035 GivenAway Products for Complete Picking", dependsOnMethods = {
            "testStartPickingGivenAway"})
    public void testPickedGivenAway() {
        Response<PickingTaskData> resp = pickingTaskClient.completePicking(currentTaskId, true);
        isResponseOk(resp);
        paymentHelper.makePaid(currentOrderId);
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.PICKED, PaymentStatusEnum.PAID);
        Thread.sleep(15000);//Added for FF delay
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.PICKED);
    }

    @Test(description = "C23748036 GiveAway Products for Partially GiveAway", dependsOnMethods = {
            "testPickedGivenAway"})
    public void testPartiallyGiveAway() {
        Response<JsonNode> resp = orderClient.giveAway(currentOrderId, false);
        isResponseOk(resp);
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.PARTIALLY_GIVEN_AWAY);
    }

    @Test(description = "C23748037 GiveAway Products for GiveAway", dependsOnMethods = {
            "testPickedGivenAway"})
    public void testGiveAway() {
        Response<JsonNode> resp = orderClient.giveAway(currentOrderId, true);
        isResponseOk(resp);
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.GIVEN_AWAY);
    }
}