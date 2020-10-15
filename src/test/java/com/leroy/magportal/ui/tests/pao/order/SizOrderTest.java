package com.leroy.magportal.ui.tests.pao.order;

import com.leroy.common_mashups.requests.customer.CustomerAccountGetRequest;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magportal.api.requests.order.OrderGetRequest;
import com.leroy.magportal.api.requests.salesdoc.SalesDocSearchV4Get;
import com.leroy.magportal.api.requests.usertasks.UserTasksRequest;
import com.leroy.magportal.ui.constants.OrderConst;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import com.leroy.magportal.ui.pages.orders.OrderCreatedInfoPage;
import com.leroy.magportal.ui.pages.orders.OrderHeaderPage;
import com.leroy.magportal.ui.tests.BaseMockUiTest;
import org.mbtest.javabank.http.predicates.PredicateType;
import org.testng.annotations.Test;

public class SizOrderTest extends BaseMockUiTest {

    @Test(description = "C23399998 Признаки ТК в листинге, на вкладке заказа")
    public void testTKSignsOnOrderTab() throws Exception {
        String customerNumber = "7666712";
        String orderTK = "201003130724";
        String orderKK = "201003130726";
        String orderPVZ = "201003130728";
        createStub(PredicateType.DEEP_EQUALS, new SalesDocSearchV4Get()
                .setPageNumber(1)
                .setPageSize(10)
                .setShopId("0" + getUserSessionData().getUserShopId())
                .setDocType(SalesDocumentsConst.Types.ORDER.getApiVal()), 0);
        createStub(PredicateType.DEEP_EQUALS, new CustomerAccountGetRequest()
                .setCustomerNumber(customerNumber)
                .setShopId(getUserSessionData().getUserShopId()), 2);
        createStub(PredicateType.EQUALS, new UserTasksRequest()
                .setProjectId("PUZ2"), 3);

        // Order TK
        createStub(PredicateType.DEEP_EQUALS, new OrderGetRequest()
                .setOrderId(orderTK)
                .setExtend(OrderGetRequest.Extend.PRODUCT_DETAILS), 1);

        // Order KK
        createStub(PredicateType.DEEP_EQUALS, new OrderGetRequest()
                .setOrderId(orderKK)
                .setExtend(OrderGetRequest.Extend.PRODUCT_DETAILS), 4);

        // Order PVZ
        createStub(PredicateType.DEEP_EQUALS, new OrderGetRequest()
                .setOrderId(orderPVZ)
                .setExtend(OrderGetRequest.Extend.PRODUCT_DETAILS), 5);

        OrderHeaderPage orderHeaderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);

        // Step 1
        step("Найти заказ ТК, открыть карточку");
        orderHeaderPage.clickDocumentInLeftMenu(orderTK);

        // Step 2
        step("Перейти на вкладку Информация - Получение - Способ получения");
        OrderCreatedContentPage orderCreatedContentPage = new OrderCreatedContentPage();
        OrderCreatedInfoPage infoPage = orderCreatedContentPage.clickInfoTab();
        infoPage.clickReceiveBtnButton()
                .shouldDeliveryTypeIs(OrderConst.DeliveryType.DELIVERY_TK);

        // Step 3
        step("Найти заказ КК, открыть карточку");
        orderHeaderPage.clickDocumentInLeftMenu(orderKK);

        // Step 4
        step("Перейти на вкладку Информация - Получение - Способ получения");
        orderCreatedContentPage = new OrderCreatedContentPage();
        infoPage = orderCreatedContentPage.clickInfoTab();
        infoPage.shouldDeliveryTypeIs(OrderConst.DeliveryType.DELIVERY_KK);

        // Step 5
        step("Найти заказ ПВЗ, открыть карточку");
        orderHeaderPage.clickDocumentInLeftMenu(orderPVZ);

        // Step 6
        step("Перейти на вкладку Информация - Получение - Способ получения");
        orderCreatedContentPage = new OrderCreatedContentPage();
        infoPage = orderCreatedContentPage.clickInfoTab();
        infoPage.shouldDeliveryTypeIs(OrderConst.DeliveryType.DELIVERY_PVZ);
    }

}
