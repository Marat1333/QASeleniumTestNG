package com.leroy.magportal.ui.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.ContextProvider;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.GiveAwayData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.data.picking.PickingTaskDataList;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.ui.constants.TestDataConstants;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.models.picking.PickingTaskData;
import com.leroy.magportal.ui.models.picking.ShortPickingTaskData;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.pages.orders.*;
import com.leroy.magportal.ui.pages.orders.widget.OrderProductControlCardWidget;
import com.leroy.magportal.ui.pages.picking.PickingContentPage;
import com.leroy.magportal.ui.pages.picking.PickingPage;
import com.leroy.magportal.ui.pages.picking.modal.SplitPickingModalStep1;
import com.leroy.magportal.ui.pages.picking.modal.SplitPickingModalStep2;
import com.leroy.magportal.ui.pages.picking.modal.SuccessfullyCreatedAssemblyModal;
import com.leroy.utils.ParserUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrdersFlowTest extends BasePAOTest {

    @Inject
    PAOHelper helper;
    @Inject
    OrderClient orderHelper;
    @Inject
    private PaymentHelper paymentHelper;

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("35");
        return sessionData;
    }

    private String orderId;
    private String pickingTaskId;

    private void initCreateOrder(int productCount, SalesDocumentsConst.States orderStatus) throws Exception {
        List<CartProductOrderData> productOrderDataList = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            CartProductOrderData productOrderData = new CartProductOrderData(productList.get(i));
            productOrderData.setQuantity(2.0);
            productOrderDataList.add(productOrderData);
        }
        switch (orderStatus) {
            case ALLOWED_FOR_PICKING:
                orderId = helper.createConfirmedOrder(productOrderDataList, true).getOrderId();
                break;
            case PICKED:
                orderId = helper.createConfirmedOrder(productOrderDataList, true).getOrderId();
                orderHelper.moveNewOrderToStatus(orderId, orderStatus);
                break;
            default:
                orderId = helper.createConfirmedOrder(productOrderDataList, false).getOrderId();
                break;
        }

    }

    private void initCreateOrder(int productCount, SalesDocumentsConst.GiveAwayPoints giveAwayPoint) throws Exception {
        List<CartProductOrderData> productOrderDataList = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            CartProductOrderData productOrderData = new CartProductOrderData(productList.get(i));
            productOrderData.setQuantity(2.0);
            productOrderDataList.add(productOrderData);
        }
        GiveAwayData giveAwayData = new GiveAwayData();
        giveAwayData.setDateAsLocalDateTime(LocalDateTime.now().plusDays(1));
        giveAwayData.setShopId(
                Integer.valueOf(ContextProvider.getContext().getUserSessionData().getUserShopId()));
        if (giveAwayPoint != null) {
            giveAwayData.setPoint(giveAwayPoint.getApiVal());
        } else {
            giveAwayData.setPoint(SalesDocumentsConst.GiveAwayPoints.PICKUP.getApiVal());
        }
        orderId = helper.createConfirmedOrder(productOrderDataList, giveAwayData, false).getOrderId();
    }

    private void initCreateOrder(int productCount, SalesDocumentsConst.GiveAwayPoints giveAwayPoint, SalesDocumentsConst.States orderStatus) throws Exception {
        List<CartProductOrderData> productOrderDataList = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            CartProductOrderData productOrderData = new CartProductOrderData(productList.get(i));
            productOrderData.setQuantity(2.0);
            productOrderDataList.add(productOrderData);
        }
        GiveAwayData giveAwayData = new GiveAwayData();
        giveAwayData.setDateAsLocalDateTime(LocalDateTime.now().plusDays(1));
        giveAwayData.setShopId(
                Integer.valueOf(ContextProvider.getContext().getUserSessionData().getUserShopId()));
        if (giveAwayPoint != null) {
            giveAwayData.setPoint(giveAwayPoint.getApiVal());
        } else {
            giveAwayData.setPoint(SalesDocumentsConst.GiveAwayPoints.PICKUP.getApiVal());
        }
        switch (orderStatus) {
            case ALLOWED_FOR_PICKING:
            case PICKED:
                orderId = helper.createConfirmedOrder(productOrderDataList, giveAwayData, true).getOrderId();
                orderHelper.moveNewOrderToStatus(orderId, orderStatus);
                break;
            default:
                orderId = helper.createConfirmedOrder(productOrderDataList, giveAwayData, false).getOrderId();
                break;
        }
    }


    private void initCreateOrder(int productCount) throws Exception {
        initCreateOrder(productCount, SalesDocumentsConst.States.CONFIRMED);
    }

    private void initFindPickingTask() throws Exception {
        OrderClient orderClient = apiClientProvider.getOrderClient();
        orderClient.waitUntilOrderGetStatus(orderId,
                SalesDocumentsConst.States.ALLOWED_FOR_PICKING, null);
        PickingTaskClient pickingTaskClient = apiClientProvider.getPickingTaskClient();
        Response<PickingTaskDataList> respPickingTasks = pickingTaskClient.searchForPickingTasks(orderId);
        assertThat(respPickingTasks, successful());
        pickingTaskId = respPickingTasks.asJson().getItems().get(0).getTaskId();
    }

    @AfterClass(enabled = false)
    private void cancelConfirmedOrder() throws Exception {
        if (orderId != null) {
            OrderClient orderClient = apiClientProvider.getOrderClient();
            Response<JsonNode> resp = orderClient.cancelOrder(orderId);
            assertThat(resp, successful());
        }
    }


    @Test(description = "C23416311 Заказы.Оффлайн.Самовывоз.Переход из статуса Готов к сборке в статус Собран", groups = NEED_PRODUCTS_GROUP)
    public void testMoveFromReadyPickingToPicked() throws Exception {

        initCreateOrder(1);

        // Step 1:
        step("Открыть страницу с Заказами");
        OrderHeaderPage orderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);
        initFindPickingTask();

        // Step 2:
        step("Ввести номер заказа из корзины и нажать кнопку 'Показать заказы'" + "Заказ" + " " + orderId);
        orderPage.enterSearchTextAndSubmit(orderId);
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 3:

        step("Кликнуть на заказ" + " " + orderId);
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
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.PICKED.getUiVal());
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


    private void testOrderOffline(SalesDocumentsConst.GiveAwayPoints giveAwayPoint) throws Exception{
        // Создать заказ в статусе "Готов к сборке"


        initCreateOrder(1,giveAwayPoint, SalesDocumentsConst.States.ALLOWED_FOR_PICKING);

        // Step 1:
        step("Открыть страницу с Заказами");
        OrderHeaderPage orderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);

        // Step 2:
        step("Найти созданный заказ с статусе 'Готов к Сборке' с номером" + " " + orderId);
        orderPage.enterSearchTextAndSubmit(orderId);
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 3:
        step("Кликнуть на заказ" + " " + orderId);
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
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.PICKED.getUiVal());
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
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.GIVEN_AWAY.getUiVal());
        orderPage.shouldDocumentCountIs(1);
    }


    @Test(description = "C23438407 Заказы. статус \"Собран\". Отображение полей на вкладке \"Контроль\". Контроль не пройден", groups = NEED_PRODUCTS_GROUP)
    public void testControlTabPickedNonControlled() throws Exception {
        initCreateOrder(1,SalesDocumentsConst.GiveAwayPoints.PICKUP, SalesDocumentsConst.States.PICKED);

        // Step 1:
        step("Открыть страницу с Заказами");
        OrderHeaderPage orderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);

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
        step("Развернуть поля карточки заказа");
        controlPage.expandProductCardFields(1);


    }



}
