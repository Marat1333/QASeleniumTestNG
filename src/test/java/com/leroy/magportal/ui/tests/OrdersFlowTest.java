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
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.PaymentMethodEnum;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.data.picking.PickingTaskDataList;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.ui.pages.orders.AssemblyOrderPage;
import com.leroy.magportal.ui.pages.orders.GiveAwayShipOrderPage;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import com.leroy.magportal.ui.pages.orders.OrderHeaderPage;
import com.leroy.magportal.ui.pages.orders.*;
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
    @Inject
    private BitrixHelper bitrixHelper;

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

    @AfterClass(enabled = false)
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
        step("Обновить список документов и проверить статус выданного заказас номером:" + orderId);
        orderPage.refreshDocumentList();
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(
                SalesDocumentsConst.States.GIVEN_AWAY.getUiVal());
        orderPage.shouldDocumentCountIs(1);
    }


    @Test(description = "C23438407 Заказы. статус \"Собран\". Отображение полей на вкладке \"Контроль\". Контроль не пройден", groups = NEED_PRODUCTS_GROUP)
    public void testControlTabPickedNonControlled() throws Exception {
        initCreateOrder(1, SalesDocumentsConst.GiveAwayPoints.PICKUP, SalesDocumentsConst.States.PICKED);

        // Step 1:
        step("Открыть страницу с Заказами");
        OrderHeaderPage orderPage = loginAndGoTo(OrderHeaderPage.class);

        // Step 2:
        step("Найти созданный заказ с статусе 'Готов к Сборке' с номером" + " " + orderId);
        orderPage.enterSearchTextAndSubmit(orderId);
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.PICKED.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 3:
        step("Кликнуть на заказ" + " " + orderId);
        orderPage.clickDocumentInLeftMenu(orderId);
        OrderCreatedContentPage createdContentPage = new OrderCreatedContentPage();
        createdContentPage.shouldOrderProductCountIs(1);

        // Step 4:
        step("Перейти на вкладку Контроль в свернутом виде");
        ControlOrderPage controlPage = createdContentPage.clickGoToControlTab();

        // Step 5:
        step("Сравнить Заказанное количество");
        controlPage.shouldOrderedQuantityIs(1, 2);

        // Step 6:
        step("Сравнить Собранное количество");
        controlPage.shouldPickedQuantityIs(1, 2);

        // Step 7:
        step("Сравнить количество на Контроль");
        controlPage.shouldControlledQuantityIs(1, 0);

    }

    @Test(description = "C22829616 Заказы. Процесс обработки заказа. Онлайн. Доставка. Предоплата", groups = NEED_PRODUCTS_GROUP)
    public void testOrderFlowOnlinePrepaymentDelivery() throws Exception {
        orderId = bitrixHelper.createOnlineOrderCardPayment(OnlineOrderTypeConst.DELIVERY_TO_DOOR).getSolutionId();
        System.out.print(orderId);

        // Step 1:
        step("Открыть страницу с Заказами");
        OrderHeaderPage orderPage = loginAndGoTo(OrderHeaderPage.class);

        // Step 2:
        step("Найти созданный заказ в статусе 'Готов к Сборке' с номером" + " " + orderId);
        orderPage.enterSearchTextAndSubmit(orderId);
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 3:
        step("Кликнуть на заказ: " + orderId);
        orderPage.clickDocumentInLeftMenu(orderId);
        OrderCreatedContentPage createdContentPage = new OrderCreatedContentPage();
        createdContentPage.shouldOrderProductCountIs(3);

        // Step 4:
        step("Перейти на Сборки");
        AssemblyOrderPage pickingTab = createdContentPage.clickGoToPickings();

        // Step 5: нажать на Сборку
        step("нажать на Сборку");
        PickingContentPage pickingContentPage = pickingTab.clickToPickingTask(1);

        // Step 6: Начать сборку
        step("Нажать на кнопку Начать сборку");
        pickingContentPage.clickStartAssemblyButton();

        // Step 7:
        step("Товар 1: Ввести в инпут Собрано количество равное,  указанному в Заказано");
        pickingContentPage.editCollectQuantity(1, 10)
                .shouldProductCollectedQuantityIs(1, 10);
        pickingContentPage.editCollectQuantity(2, 10)
                .shouldProductCollectedQuantityIs(2, 10);
        pickingContentPage.editCollectQuantity(3, 10)
                .shouldProductCollectedQuantityIs(3, 10);

        // Step 8:
        step("Завершить сборку");
        pickingContentPage.clickFinishAssemblyButton();

        // Step 9:
        step("Оплатить заказ через TPNET");
        paymentHelper.makePayment(orderId, PaymentMethodEnum.TPNET);

        // Step 10:
        step("Вернуться на страницу заказов ");
        PickingPage pickingPage = new PickingPage();
        pickingPage.clickOrderLinkAndGoToOrderPage();

        // Step 10:
        step("Проверить статус собранного заказа");
        orderClient.waitUntilOrderGetStatus(orderId, SalesDocumentsConst.States.PICKED, PaymentStatusEnum.PAID);
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
        giveAwayShipOrderPage.editToShipQuantity(1, 10)
                .shouldProductToShipQuantityIs(1, 10);
        giveAwayShipOrderPage.editToShipQuantity(2, 10)
                .shouldProductToShipQuantityIs(2, 10);
        giveAwayShipOrderPage.editToShipQuantity(3, 10)
                .shouldProductToShipQuantityIs(3, 10);

        // Step 13
        step("Нажать на кнопку 'Отгрузить'");
        giveAwayShipOrderPage.clickShipButton();

        // Step 14:
        step("Обновить список документов и проверить статус выданного заказас номером:" + orderId);
        orderPage.refreshDocumentList();
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(
                SalesDocumentsConst.States.SHIPPED.getUiVal());
        orderPage.shouldDocumentCountIs(1);

    }

    @Test(description = "C22829616 Заказы. Процесс обработки заказа. Онлайн. Предоплата. Самовывоз", groups = NEED_PRODUCTS_GROUP)
    public void testOrderFlowOnlinePrepaymentPickUp() throws Exception {
        orderId = bitrixHelper.createOnlineOrderCardPayment(OnlineOrderTypeConst.PICKUP_PREPAYMENT).getSolutionId();
        System.out.print(orderId);

        // Step 1:
        step("Открыть страницу с Заказами");
        OrderHeaderPage orderPage = loginAndGoTo(OrderHeaderPage.class);
        //orderId = bitrixHelper.createOnlineOrderCardPayment(OnlineOrderTypeConst.DELIVERY_TO_DOOR).getSolutionId();

        // Step 2:
        step("Найти созданный заказ в статусе 'Готов к Сборке' с номером" + " " + orderId);
        orderPage.enterSearchTextAndSubmit(orderId);
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 2:
        //orderId = "210103322645";
        step("Найти созданный заказ в статусе 'Готов к Сборке' с номером" + " " + orderId);
        orderPage.enterSearchTextAndSubmit(orderId);
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(
                SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 3:

        step("Кликнуть на заказ: " + orderId);
        orderPage.clickDocumentInLeftMenu(orderId);
        OrderCreatedContentPage createdContentPage = new OrderCreatedContentPage();
        createdContentPage.shouldOrderProductCountIs(3);

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
        pickingContentPage.editCollectQuantity(1, 10)
                .shouldProductCollectedQuantityIs(1, 10);
        pickingContentPage.editCollectQuantity(2, 10)
                .shouldProductCollectedQuantityIs(2, 10);
        pickingContentPage.editCollectQuantity(3, 10)
                .shouldProductCollectedQuantityIs(3, 10);

        // Step 8:
        step("Завершить сборку");
        pickingContentPage.clickFinishAssemblyButton();

        // Step 9:
        step("Оплатить заказ через API");
        paymentHelper.makePayment(orderId, PaymentMethodEnum.API);

        // Step 10:
        step("Вернуться на страницу заказов ");
        PickingPage pickingPage = new PickingPage();
        pickingPage.clickOrderLinkAndGoToOrderPage();

        // Step 11:
        step("Проверить статус собранного заказа");
        orderPage.refreshDocumentList();
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(
                SalesDocumentsConst.States.PICKED_WAIT.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 12:
        step("Оплатить заказ через TPNET");
        paymentHelper.makePayment(orderId, PaymentMethodEnum.TPNET);

        // Step 13:
        step("Перейти на вкладку 'К выдаче и возврату'");
        GiveAwayShipOrderPage giveAwayShipOrderPage = createdContentPage.clickGoToShipRefund();

        // Step 14:
        step("Товар 1: Ввести в инпут 'К выдаче' количество равное,  указанному в Заказано");
        giveAwayShipOrderPage.editToShipQuantity(1, 10)
                .shouldProductToShipQuantityIs(1, 10);
        giveAwayShipOrderPage.editToShipQuantity(2, 10)
                .shouldProductToShipQuantityIs(2, 10);
        giveAwayShipOrderPage.editToShipQuantity(3, 10)
                .shouldProductToShipQuantityIs(3, 10);

        // Step 15:
        step("Нажать на кнопку 'Выдать'");
        giveAwayShipOrderPage.clickGiveAwayButton();

        // Step 16:
        step("Обновить список документов и проверить статус выданного заказас номером:" + orderId);
        orderPage.refreshDocumentList();
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(
                SalesDocumentsConst.States.GIVEN_AWAY.getUiVal());
        orderPage.shouldDocumentCountIs(1);
    }



}


