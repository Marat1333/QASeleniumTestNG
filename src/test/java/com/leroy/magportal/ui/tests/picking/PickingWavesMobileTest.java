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
import com.leroy.magportal.ui.pages.picking.mobile.PickingDocListMobilePage;
import com.leroy.magportal.ui.tests.BaseMockMagPortalUiTest;
import com.leroy.magportal.ui.tests.BasePAOTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.leroy.constants.sales.SalesDocumentsConst.States.PICKED;
import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

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

    @Test(description = "C23415580 Добавление сборок в волну"/*, groups = NEED_PRODUCTS_GROUP*/)
    public void testAddPickingIntoWave() throws Exception {
        setUpMockForTestCase();
        /*initCreateOrder(1);
        String order1 = orderId;
        initFindPickingTask();
        String assemblyNumber = pickingTaskId.substring(pickingTaskId.length() - 4);
        String orderNumber = orderId.substring(orderId.length() - 4);
        String fullNumber1 = assemblyNumber + " *" + orderNumber;
        initCreateOrder(1);
        initFindPickingTask();
        assemblyNumber = pickingTaskId.substring(pickingTaskId.length() - 4);
        orderNumber = orderId.substring(orderId.length() - 4);
        String fullNumber2 = assemblyNumber + " *" + orderNumber;
        String order2 = orderId;*/

        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);
        pickingPage.reloadPage();

        // Step 2
        step("Нажать на иконку волны сборок");
    }

}
