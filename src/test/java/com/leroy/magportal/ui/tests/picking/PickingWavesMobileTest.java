package com.leroy.magportal.ui.tests.picking;

import com.leroy.magportal.ui.pages.picking.mobile.PickingDocListMobilePage;
import com.leroy.magportal.ui.pages.picking.mobile.PickingWaveMobilePage;
import com.leroy.magportal.ui.tests.BaseMockMagPortalUiTest;
import org.testng.annotations.Test;

public class PickingWavesMobileTest extends BaseMockMagPortalUiTest {

    /*@Inject
    PAOHelper helper;
    @Inject
    OrderClient orderHelper;

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("49");
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

    @AfterClass(enabled = false)
    private void cancelConfirmedOrder() throws Exception {
        if (orderId != null) {
            OrderClient orderClient = apiClientProvider.getOrderClient();
            Response<JsonNode> resp = orderClient.cancelOrder(orderId);
            assertThat(resp, successful());
        }
    }*/

    @Test(description = "C23415580 Добавление сборок в волну")
    public void testAddPickingIntoWave() throws Exception {
        setUpMockForTestCase();

        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);
        pickingPage.reloadPage();
        pickingPage = new PickingDocListMobilePage();

        // Step 2
        step("Нажать на иконку волны сборок");
        PickingWaveMobilePage pickingWaveMobilePage = pickingPage.clickPickingWageButton();

        // Step 3
        step("Проверить список товаров, если они есть");
        pickingWaveMobilePage.shouldTitleIsVisible();

        // Step 4
        step("Вернуться на экран Сборки");
        pickingWaveMobilePage.clickBackButton();

        // Step 5
        step("Выбрать несколько сборок, нажав на чекбокс 'Выбрать для сборки'");
        pickingPage = new PickingDocListMobilePage();
        pickingPage.selectAllPickingChkBoxes()
                .checkActiveButtonsVisibility(true);

        // Step 6
        step("Нажать кнопку 'Отмена'");
        pickingPage.clickCancelButton()
                .checkActiveButtonsVisibility(false);

        // Step 7
        step("Выбрать несколько сборок, нажав на чекбокс 'Выбрать для сборки', Нажать кнопку 'Начать сборку'");
        pickingPage.selectAllPickingChkBoxes()
                .clickStartPicking()
                .checkActiveButtonsVisibility(false);

        // Step 8
        step("Нажать на иконку волны сборок");
        pickingPage.clickPickingWageButton()
                .shouldTitleIsVisible();
    }

}
