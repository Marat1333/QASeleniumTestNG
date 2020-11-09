package com.leroy.magportal.ui.tests.picking;

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
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.picking.ShortPickingTaskData;
import com.leroy.magportal.ui.pages.picking.mobile.PickingContentMobilePage;
import com.leroy.magportal.ui.pages.picking.mobile.PickingDocListMobilePage;
import com.leroy.magportal.ui.pages.picking.mobile.PickingPlaceOrderMobileModal;
import com.leroy.magportal.ui.tests.BaseMockMagPortalUiTest;
import com.leroy.magportal.ui.tests.BasePAOTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class PickingMobileTest extends BaseMockMagPortalUiTest {

    /*@Inject
    PAOHelper helper;
    @Inject
    OrderClient orderHelper;

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        //sessionData.setUserShopId("35");
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
        giveAwayData.setDate(LocalDateTime.now().plusDays(1));
        giveAwayData.setShopId(
                Integer.valueOf(ContextProvider.getContext().getUserSessionData().getUserShopId()));
        if (giveAwayPoint != null) {
            giveAwayData.setPoint(giveAwayPoint.getApiVal());
        } else {
            giveAwayData.setPoint(SalesDocumentsConst.GiveAwayPoints.PICKUP.getApiVal());
        }
        orderId = helper.createConfirmedOrder(productOrderDataList, giveAwayData, false).getOrderId();
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

    @AfterClass(enabled = true)
    private void cancelConfirmedOrder() throws Exception {
        if (orderId != null) {
            OrderClient orderClient = apiClientProvider.getOrderClient();
            Response<JsonNode> resp = orderClient.cancelOrder(orderId);
            assertThat(resp, successful());
        }
    }*/

    @BeforeMethod
    public void setUpMock() throws Exception {
        //setUpMockForTestCase();
    }

    @Test(description = "C23438168 Сборка. Фильтрация по Типу сборки")
    public void testFiltersByAssemblyType() throws Exception {

        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Нажать на кебаб меню справа, выставить в фильтрах тип сборки 'Торг.зал LS', нажать 'Применить'");
        pickingPage = pickingPage.clickFilterButton()
                .selectAssemblyType(PickingConst.AssemblyType.SHOPPING_ROOM)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByAssemblyType(PickingConst.AssemblyType.SHOPPING_ROOM);

        // Step 3
        step("Повторить шаг 2 для остальных типов сборки Склад WH , CC");
        pickingPage = pickingPage.clickFilterButton()
                .selectAssemblyType(PickingConst.AssemblyType.STOCK)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByAssemblyType(PickingConst.AssemblyType.STOCK);
        pickingPage.clickFilterButton()
                .selectAssemblyType(PickingConst.AssemblyType.SS)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByAssemblyType(PickingConst.AssemblyType.SS);

        // Step 4
        step("Деактивировать все фильтры, нажать Применить");
        pickingPage.clickFilterButton()
                .clickClearFilters()
                .clickConfirmBtn();
        String s = "";
    }

    @Test(description = "C23438169 Сборка. Фильтрация по Статусу сборки")
    public void testFiltersByStatus() throws Exception {

        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Нажать на кебаб меню справа, выставить в фильтрах Статус заказа 'Готов к сборке', нажать 'Применить'");
        pickingPage = pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.ALLOWED_FOR_PICKING)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.ALLOWED_FOR_PICKING);

        // Step 3
        step("Повторить шаг 2 для остальных статусов Сборка, Сборка(пауза), Част.собран, Собран");
        pickingPage = pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS);
        pickingPage = pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.PAUSE_PICKING)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PAUSE_PICKING);
        pickingPage = pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.PICKED)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PICKED);
        pickingPage = pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED);

        // Step 4
        step("Активировать все статусы сборки, нажать Применить");
        pickingPage = pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.ALLOWED_FOR_PICKING,
                        SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS,
                        SalesDocumentsConst.PickingStatus.PAUSE_PICKING,
                        SalesDocumentsConst.PickingStatus.PICKED,
                        SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED)
                .clickConfirmBtn();
        // TODO

        // Step 5
        step("Нажать на кебаб меню справа, нажать 'Очистить', 'Применить'");
        // TODO Надо на моках проверить как-то что фильтры сброшены
    }

    @Test(description = "C23438170 Сборка. Фильтрация по отделам")
    public void testFiltersByDepartments() throws Exception {

        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Нажать на кебаб меню справа, выставить в фильтрах Отделы '01 Стройматериалы', нажать 'Применить'");
        pickingPage = pickingPage.clickFilterButton()
                .clickClearFilters()
                .selectDepartmentFilter("01")
                .clickConfirmBtn()
                .shouldDocumentsFilteredByDepartments("01");

        // Step 3
        step("Выставить в фильтрах Отделы от 1 до 15, нажать 'Применить'");
        String[] filtersForStep3 = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15"};
        pickingPage = pickingPage.clickFilterButton()
                .clickClearFilters()
                .selectDepartmentFilter(filtersForStep3)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByDepartments(filtersForStep3);

        // Step 4
        step("В фильтрах Отделы оставить активным только несколько отделов, выбрать Статус заказа, выбрать Тип сборки, нажать 'Применить'");
        String[] filtersForStep4 = {"01", "05"};
        pickingPage = pickingPage.clickFilterButton()
                .clickClearFilters()
                .selectDepartmentFilter("01", "05")
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.ALLOWED_FOR_PICKING)
                .selectAssemblyType(PickingConst.AssemblyType.SHOPPING_ROOM)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByDepartments(filtersForStep4)
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.ALLOWED_FOR_PICKING)
                .shouldDocumentsFilteredByAssemblyType(PickingConst.AssemblyType.SHOPPING_ROOM);

        // Step 5
        step("Нажать на кебаб меню справа, нажать 'Очистить', 'Применить'");
        // TODO Надо на моках проверить как-то что фильтры сброшены
    }

    @Test(description = "C23438171 Сборка. Фильтрация с чек боксом 'Мои'")
    public void testFilterMy() throws Exception {
        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Нажать на кебаб меню справа, активировать чек бокс 'Мои', выставить статус заказа Сборка, Сборка Пауза, Частично собран");
        pickingPage = pickingPage.clickFilterButton()
                .clickClearFilters()
                .selectFilterMy()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS,
                        SalesDocumentsConst.PickingStatus.PAUSE_PICKING,
                        SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS,
                        SalesDocumentsConst.PickingStatus.PAUSE_PICKING,
                        SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED)
                .shouldDocumentsFilteredByMy();

        // Step 3
        step("Добавить к фильтрам отделы 01, 02, нажать Применить");
        pickingPage.clickFilterButton()
                .selectDepartmentFilter("01", "02")
                .clickConfirmBtn()
                .shouldDocumentsFilteredByMy()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS,
                        SalesDocumentsConst.PickingStatus.PAUSE_PICKING,
                        SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED)
                .shouldDocumentsFilteredByDepartments("01", "02");
    }

    @Test(description = "C23438174 Сборка. Поиск по номеру заказа")
    public void testSearchForPickingByOrderNumber() throws Exception {
        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Ввести 12-значный номер заказа");
        ShortPickingTaskData shortPickingTaskData = new ShortPickingTaskData();
        pickingPage.searchForPickingByOrderNumber("201101265132")
                .shouldDocumentListIs(Collections.singletonList(shortPickingTaskData));
    }

    // TODO надо найти валидные данные под разные схемы
    @Test(description = "C23438175 Сборка. Фильтрация по Схеме продажи", enabled = false)
    public void testFilteringBySalesScheme() throws Exception {
        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Нажать на кебаб меню справа, выставить в фильтрах тип сборки 'LT', нажать 'Применить'");
        pickingPage.clickFilterButton()
                .selectSalesScheme("LT");

        // Step 3
        step("Повторить шаг 2 для остальных cхем продажи");

        // Step 4
        step("Повторить шаг 2 для нескольких cхем продажи одновременно");

        // Step 5
        step("Деактивировать все фильтры, нажать Применить");
    }

    // TODO НАПОСЛЕДОК
    /*@Test(description = "C23438204 Размещение заказа", groups = NEED_PRODUCTS_GROUP)
    public void testPlacingAnOrder() throws Exception {
        initCreateOrder(1, SalesDocumentsConst.States.PICKED);
        //String s = "201103194179";

        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Найти сборку из предусловия, зайти в карточку сборки");
        //pickingPage.clickDocumentInLeftMenu("9485 *4179");

        // Step 3
        step("Нажать на кнопку 'Разместить'");
        PickingContentMobilePage pickingContentMobilePage = new PickingContentMobilePage();
        PickingPlaceOrderMobileModal placeOrderMobileModal = pickingContentMobilePage.clickPlaceButton();

        // Step 4
        step("В выпадающем списке с зоной размешения выбрать Передачи в логистику");
        placeOrderMobileModal.selectZone(PickingPlaceOrderMobileModal.Zone.TRANSFER_TO_LOGISTIC)
                .shouldPlaceOptionIs("BU001:Лифт");

        // Step 5
        step("Нажать кнопку 'Разместить'");
        pickingContentMobilePage = placeOrderMobileModal.clickPlaceButton();

        // Step 6
        step("Нажать кнопку 'Изменить размещение'");
        placeOrderMobileModal = pickingContentMobilePage.clickPlaceButton();

        // Step 7
        step("В выпадающем списке зоны выбрать 'Клиентских заказов'");
        placeOrderMobileModal.selectZone(PickingPlaceOrderMobileModal.Zone.CUSTOMER_ORDERS)
                .shouldPlaceOptionIsClear();

        // Step 8 - 9
        step("Проставить чекбокс в нескольких позиций и нажать на крестик");
        placeOrderMobileModal.selectPlace(1, false)
                .selectPlace(2, true);
        List<String> selectedPlaces = placeOrderMobileModal.getSelectedPlaces();

        // Step 10
        step("Нажать кнопку 'Разместить'");
        pickingContentMobilePage = placeOrderMobileModal.clickPlaceButton();

        // Step 11
        step("Через кнопку в хедере 'Назад' вернуться в список сборок и доскроллить до собранной сборки");

        String sk = "";
    }*/

}
