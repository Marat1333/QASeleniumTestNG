package com.leroy.magportal.ui.tests;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.constants.sales.SalesDocumentsConst.GiveAwayPoints;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.data.picking.PickingTaskDataList;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.ui.pages.orders.AssemblyOrderPage;
import com.leroy.magportal.ui.pages.orders.GiveAwayShipOrderPage;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import com.leroy.magportal.ui.pages.orders.OrderHeaderPage;
import com.leroy.magportal.ui.pages.picking.PickingContentPage;
import com.leroy.magportal.ui.pages.picking.PickingPage;
import java.util.List;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class OrdersFlowTest extends BasePAOTest {

    @Inject
    private PAOHelper helper;
    @Inject
    private OrderClient orderClient;
    @Inject
    private PaymentHelper paymentHelper;
    @Inject
    private PickingTaskClient pickingTaskClient;

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("35");
        return sessionData;
    }

    private String orderId;
    private String pickingTaskId;

    private void initCreateOrder(int productCount, SalesDocumentsConst.States orderStatus) {
        List<CartProductOrderData> productOrderDataList = makeCartProductsList(productCount, 2.0);
        switch (orderStatus) {
            case ALLOWED_FOR_PICKING:
                orderId = helper.createConfirmedOrder(productOrderDataList, true).getOrderId();
                break;
            case PICKED:
                orderId = helper.createConfirmedOrder(productOrderDataList, true).getOrderId();
                orderClient.moveNewOrderToStatus(orderId, orderStatus);
                break;
            default:
                orderId = helper.createConfirmedOrder(productOrderDataList, false).getOrderId();
                break;
        }

    }

    private void initCreateOrder(int productCount,
            SalesDocumentsConst.GiveAwayPoints giveAwayPoint) {

        if (giveAwayPoint == null) {
            giveAwayPoint = GiveAwayPoints.PICKUP;
        }

        orderId = helper
                .createConfirmedOrder(makeCartProductsList(productCount, 2.0), giveAwayPoint, false)
                .getOrderId();
    }

    private void initCreateOrder(int productCount, SalesDocumentsConst.GiveAwayPoints giveAwayPoint,
            SalesDocumentsConst.States orderStatus) {
        List<CartProductOrderData> productOrderDataList = makeCartProductsList(productCount, 2.0);

        if (giveAwayPoint == null) {
            giveAwayPoint = GiveAwayPoints.PICKUP;
        }

        switch (orderStatus) {
            case ALLOWED_FOR_PICKING:
            case PICKED:
                orderId = helper.createConfirmedOrder(productOrderDataList, giveAwayPoint, true)
                        .getOrderId();
                orderClient.moveNewOrderToStatus(orderId, orderStatus);
                break;
            default:
                orderId = helper.createConfirmedOrder(productOrderDataList, giveAwayPoint, false)
                        .getOrderId();
                break;
        }
    }


    private void initCreateOrder(int productCount) {
        initCreateOrder(productCount, SalesDocumentsConst.States.CONFIRMED);
    }

    private void initFindPickingTask() {
        orderClient.waitUntilOrderGetStatus(orderId,
                SalesDocumentsConst.States.ALLOWED_FOR_PICKING, null);
        Response<PickingTaskDataList> respPickingTasks = pickingTaskClient
                .searchForPickingTasks(orderId);
        assertThat(respPickingTasks, successful());
        pickingTaskId = respPickingTasks.asJson().getItems().get(0).getTaskId();
    }

    @AfterClass(enabled = true)
    private void cancelConfirmedOrder() {
        if (orderId != null) {
            Response<JsonNode> resp = orderClient.cancelOrder(orderId);
            assertThat(resp, successful());
        }
    }


    @Test(description = "C23416311 Заказы.Оффлайн.Самовывоз.Переход из статуса Готов к сборке в статус Собран", groups = NEED_PRODUCTS_GROUP)
    public void testMoveFromReadyPickingToPicked() throws Exception {

        initCreateOrder(1);

        // Step 1:
        step("Открыть страницу с Заказами");
        OrderHeaderPage orderPage = loginAndGoTo(OrderHeaderPage.class);
        //initFindPickingTask();

        // Step 2:
        step("Ввести номер заказа из корзины и нажать кнопку Поиска. Заказ: " + orderId);
        orderPage.enterSearchTextAndSubmit(orderId);
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(
                SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 3:

        step("Кликнуть на заказ: " + orderId);
        orderPage.clickDocumentInLeftMenu(orderId);
        OrderCreatedContentPage createdContentPage = new OrderCreatedContentPage();
        createdContentPage.shouldOrderProductCountIs(1);

        // Step 4:

        step("Перейти на Сборки");
        AssemblyOrderPage pickingTab = createdContentPage.clickGoToPickings();

        // Step5: нажать на Сборку
        step("нажать на Сборку");
        PickingContentPage pickingContentPage = pickingTab.clickToPickingTask(1);

        // Step6: Начать сборку
        step("Нажать на кнопку Начать сборку");
        pickingContentPage.clickStartAssemblyButton();

        // Step7:
        step("Товар 1: Ввести в инпут Собрано количество равное,  указанному в Заказано");
        pickingContentPage.editCollectQuantity(1, 2)
                .shouldProductCollectedQuantityIs(1, 2);

        // Step 8:
        step("Завершить сборку");
        pickingContentPage.clickFinishAssemblyButton();

        // Step 9:
        step("Вернуться на страницу заказов ");
        PickingPage pickingPage = new PickingPage();
        pickingPage.clickOrderLinkAndGoToOrderPage();

        // Step 10:
        step("Проверить статус собранного заказа");
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(
                SalesDocumentsConst.States.PICKED.getUiVal());
        orderPage.shouldDocumentCountIs(1);
    }


    @Test(description = "C23428132 Заказы.Оффлайн.Самовывоз.", groups = NEED_PRODUCTS_GROUP)
    public void testOrderOfflinePickup() throws Exception {
        testOrderOffline(SalesDocumentsConst.GiveAwayPoints.PICKUP);
    }


    @Test(description = "C23437677 Заказы. Oффлайн. Доставка.", groups = NEED_PRODUCTS_GROUP)
    public void testOrderOfflineDelivery() throws Exception {
        testOrderOffline(SalesDocumentsConst.GiveAwayPoints.DELIVERY);
    }


    private void testOrderOffline(SalesDocumentsConst.GiveAwayPoints giveAwayPoint)
            throws Exception {
        // Создать заказ в статусе "Готов к сборке"

        initCreateOrder(1, giveAwayPoint, SalesDocumentsConst.States.ALLOWED_FOR_PICKING);

        // Step 1:
        step("Открыть страницу с Заказами");
        OrderHeaderPage orderPage = loginAndGoTo(OrderHeaderPage.class);

        // Step 2:
        step("Найти созданный заказ с статусе 'Готов к Сборке' с номером: " + orderId);
        orderPage.enterSearchTextAndSubmit(orderId);
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(
                SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 3:
        step("Кликнуть на заказ: " + orderId);
        orderPage.clickDocumentInLeftMenu(orderId);
        OrderCreatedContentPage createdContentPage = new OrderCreatedContentPage();
        createdContentPage.shouldOrderProductCountIs(1);

        // Step 4:
        step("Перейти на Сборки");
        AssemblyOrderPage pickingTab = createdContentPage.clickGoToPickings();

        // Step5: нажать на Сборку
        step("нажать на Сборку");
        PickingContentPage pickingContentPage = pickingTab.clickToPickingTask(1);

        // Step6: Начать сборку
        step("Нажать на кнопку Начать сборку");
        pickingContentPage.clickStartAssemblyButton();

        // Step7:
        step("Товар 1: Ввести в инпут Собрано количество равное,  указанному в Заказано");
        pickingContentPage.editCollectQuantity(1, 2)
                .shouldProductCollectedQuantityIs(1, 2);

        // Step 8:
        step("Завершить сборку");
        pickingContentPage.clickFinishAssemblyButton();
        paymentHelper.makePaid(orderId);

        // Step 9:
        step("Вернуться на страницу заказов ");
        PickingPage pickingPage = new PickingPage();
        pickingPage.clickOrderLinkAndGoToOrderPage();

        // Step 10:
        step("Проверить статус собранного заказа");
        orderPage.refreshDocumentList();
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(
                SalesDocumentsConst.States.PICKED.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 11:
        step("Перейти на вкладку 'К выдаче и возврату'");
        GiveAwayShipOrderPage giveAwayShipOrderPage = createdContentPage.clickGoToShipRefund();

        // Step 12:
        step("Товар 1: Ввести в инпут 'К выдаче' количество равное,  указанному в Заказано");
        giveAwayShipOrderPage.editToShipQuantity(1, 2)
                .shouldProductToShipQuantityIs(1, 2);

        // Step 13
        step("Нажать на кнопку 'Выдать'");
        giveAwayShipOrderPage.clickGiveAwayButton();

        // Step 14:
        step("Обновить список документов и проверить статус выданного заказа");
        orderPage.refreshDocumentList();
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(
                SalesDocumentsConst.States.GIVEN_AWAY.getUiVal());
        orderPage.shouldDocumentCountIs(1);
    }
}
