package com.leroy.magportal.api.tests.onlineOrders.other;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.data.onlineOrders.OrderProductsToGivenAwayData;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.AllureId;
import lombok.SneakyThrows;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.customerorders.enums.PaymentStatus;

public class PostpaymentToGiveAwayTest extends BaseMagPortalApiTest {

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
                .stream().findFirst().orElse(new PickingTaskData()).getTaskId();
    }

    @Test(description = "C23748043 Pickup: GiveAway for Start Picking")
    @AllureId("16142")
    public void testStartPickingGiveAway() {
        Response<?> resp = pickingTaskClient.startPicking(currentTaskId);
        isResponseOk(resp);
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.PICKING_IN_PROGRESS);
    }

    @SneakyThrows
    @Test(description = "C23748046 Pickup: GiveAway for Picked", dependsOnMethods = {
            "testStartPickingGiveAway"})
    @AllureId("16143")
    public void testPickedGiveAway() {
        Response<?> resp = pickingTaskClient.completePicking(currentTaskId, true);
        paymentHelper.makePaid(currentOrderId);
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.PICKED, PaymentStatus.PAID);
        isResponseOk(resp);
        Thread.sleep(15000);//Added for FF delay
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.PICKED);
    }

    @Test(description = "C23748044 Pickup: GiveAway Products for Partially GiveAway", dependsOnMethods = {
            "testPickedGiveAway"})
    @AllureId("16144")
    public void testPartiallyGiveAway() {
        Response<?> resp = orderClient.giveAway(currentOrderId, false);
        isResponseOk(resp);
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.PARTIALLY_GIVEN_AWAY);
    }

    @Test(description = "C23748045 Pickup: GiveAway Products for GiveAway", dependsOnMethods = {
            "testPickedGiveAway"})
    @AllureId("16145")
    public void testGiveAway() {
        Response<?> resp = orderClient.giveAway(currentOrderId, true);
        isResponseOk(resp);
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.GIVEN_AWAY);
    }
}