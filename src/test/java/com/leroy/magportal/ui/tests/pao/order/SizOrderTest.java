package com.leroy.magportal.ui.tests.pao.order;

import com.google.inject.Inject;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.constants.OrderConst;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import com.leroy.magportal.ui.pages.orders.OrderCreatedInfoPage;
import com.leroy.magportal.ui.pages.orders.OrderHeaderPage;
import org.testng.annotations.Test;

public class SizOrderTest extends WebBaseSteps {

    @Inject
    private BitrixHelper bitrixHelper;

    @Test(description = "C23399998 Признаки ТК в листинге, на вкладке заказа")
    public void testTKSignsOnOrderTab() throws Exception {
        OnlineOrderTypeConst.OnlineOrderTypeData currentOrderType = OnlineOrderTypeConst.DELIVERY_TK;
        String orderDeliveryTK = bitrixHelper.createOnlineOrder(currentOrderType).getSolutionId();

        currentOrderType = OnlineOrderTypeConst.DELIVERY_KK;
        String orderDeliveryKK = bitrixHelper.createOnlineOrder(currentOrderType).getSolutionId();

        currentOrderType = OnlineOrderTypeConst.DELIVERY_PVZ;
        String orderDeliveryPVZ = bitrixHelper.createOnlineOrder(currentOrderType).getSolutionId();

        OrderHeaderPage orderHeaderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);
        // Step 1
        step("Найти заказ ТК, открыть карточку");
        orderHeaderPage.clickDocumentInLeftMenu(orderDeliveryTK);

        // Step 2
        step("Перейти на вкладку Информация - Получение - Способ получения");
        OrderCreatedContentPage orderCreatedContentPage = new OrderCreatedContentPage();
        OrderCreatedInfoPage infoPage = orderCreatedContentPage.clickInfoTab();
        infoPage.clickReceiveBtnButton()
                .shouldDeliveryTypeIs(OrderConst.DeliveryType.DELIVERY_TK);

        // Step 3
        step("Найти заказ КК, открыть карточку");
        orderHeaderPage.clickDocumentInLeftMenu(orderDeliveryKK);

        // Step 4
        step("Перейти на вкладку Информация - Получение - Способ получения");
        orderCreatedContentPage = new OrderCreatedContentPage();
        infoPage = orderCreatedContentPage.clickInfoTab();
        infoPage.shouldDeliveryTypeIs(OrderConst.DeliveryType.DELIVERY_KK);

        // Step 5
        step("Найти заказ ПВЗ, открыть карточку");
        orderHeaderPage.clickDocumentInLeftMenu(orderDeliveryPVZ);

        // Step 6
        step("Перейти на вкладку Информация - Получение - Способ получения");
        orderCreatedContentPage = new OrderCreatedContentPage();
        infoPage = orderCreatedContentPage.clickInfoTab();
        infoPage.shouldDeliveryTypeIs(OrderConst.DeliveryType.DELIVERY_PVZ);
    }

}
