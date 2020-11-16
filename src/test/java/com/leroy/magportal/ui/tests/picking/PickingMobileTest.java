package com.leroy.magportal.ui.tests.picking;

import com.leroy.magportal.ui.tests.BaseMockMagPortalUiTest;
import org.testng.annotations.BeforeMethod;

public class PickingMobileTest extends BaseMockMagPortalUiTest {

    /*@Inject
    PAOHelper helper;
    @Inject
    OrderClient orderHelper;
    @Inject
            PickingTaskClient pickingTaskClient;

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
        setUpMockForTestCase();
    }

    // TODO НАПОСЛЕДОК
    /*@Test(description = "C23438204 Размещение заказа", groups = NEED_PRODUCTS_GROUP)
    public void testPlacingAnOrder() throws Exception {
        initCreateOrder(1, SalesDocumentsConst.States.PICKED);

        Response<PickingTaskDataList> respPickingTasks = pickingTaskClient.searchForPickingTasks(orderId);
        assertThat(respPickingTasks, successful());
        pickingTaskId = respPickingTasks.asJson().getItems().get(0).getTaskId();

        String assemblyNumber = pickingTaskId.substring(pickingTaskId.length() - 4);
        String orderNumber = orderId.substring(orderId.length() - 4);
        String fullNumber = assemblyNumber + " *" + orderNumber;

        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Найти сборку из предусловия, зайти в карточку сборки");
        pickingPage.clickDocumentInLeftMenu(fullNumber);

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
