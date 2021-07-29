package com.leroy.magportal.ui.tests.pao.order;

import com.leroy.common_mashups.customer_accounts.requests.CustomerAccountGetRequest;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.UserSessionData;
import com.leroy.magportal.api.requests.order.OrderGetRequest;
import com.leroy.magportal.api.requests.salesdoc.SalesDocSearchV4Get;
import com.leroy.magportal.api.requests.usertasks.UserTasksGetRequest;
import com.leroy.magportal.ui.constants.OrderConst;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import com.leroy.magportal.ui.pages.orders.OrderCreatedInfoPage;
import com.leroy.magportal.ui.pages.orders.OrderHeaderPage;
import com.leroy.magportal.ui.tests.BaseMockMagPortalUiTest;
import io.qameta.allure.AllureId;
import org.mbtest.javabank.http.predicates.PredicateType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SizOrderTest extends BaseMockMagPortalUiTest {

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData userSessionData = super.initTestClassUserSessionDataTemplate();
        userSessionData.setUserShopId("139");
        return userSessionData;
    }

    private String notPaidStatus = "Не оплачен";
    private String holdStatus = "Холд средств";
    private String paidStatus = "Оплачен";

    private SalesDocSearchV4Get buildGetSingleDocSearchOrderRequest(String orderId) {
        return new SalesDocSearchV4Get()
                .setPageNumber(1)
                .setPageSize(10)
                .setDocId(orderId)
                .setShopId(getUserSessionData().getUserShopId())
                .setDocType(SalesDocumentsConst.Types.ORDER.getApiVal());
    }

    private OrderGetRequest buildOrderGetRequest(String orderId) {
        return new OrderGetRequest()
                .setOrderId(orderId)
                .setExtend(OrderGetRequest.Extend.PRODUCT_DETAILS);
    }

    private void verifyOrderStatus(
            OrderHeaderPage orderHeaderPage, String orderId,
            String expectedOrderStatus, String expectedPaymentStatus) throws Exception {
        orderHeaderPage.enterSearchTextAndSubmit(orderId);
        orderHeaderPage.shouldDocumentCountIs(1);
        orderHeaderPage.shouldDocumentListContainsOnlyWithStatuses(expectedOrderStatus);
        orderHeaderPage.clickDocumentInLeftMenu(orderId);
        OrderCreatedContentPage orderCreatedContentPage = new OrderCreatedContentPage();
        orderCreatedContentPage.shouldOrderStatusIs(expectedOrderStatus);
        orderCreatedContentPage.shouldOrderPaymentStatusIs(expectedPaymentStatus);
    }

    @BeforeClass
    public void setUpMock() throws Exception {
        String customerNumber = "7666712";
        createStub(PredicateType.DEEP_EQUALS, new CustomerAccountGetRequest()
                .setCustomerNumber(customerNumber)
                .setShopId(getUserSessionData().getUserShopId()), 2);
        createStub(PredicateType.EQUALS, new UserTasksGetRequest()
                .setProjectId("PUZ2"), 3);
    }

    // ------------------- TEST CASES ------------------------------- //

    @Test(description = "C23399998 Признаки ТК в листинге, на вкладке заказа")
    public void testTKSignsOnOrderTab() throws Exception {
        String orderTK = "201003130724";
        String orderKK = "201003130726";
        String orderPVZ = "201003130728";
        createStub(PredicateType.DEEP_EQUALS, new SalesDocSearchV4Get()
                .setPageNumber(1)
                .setPageSize(10)
                .setShopId(getUserSessionData().getUserShopId())
                .setDocType(SalesDocumentsConst.Types.ORDER.getApiVal()), 0);

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

    @Test(description = "C23426982 Статус 'В доставке'")
    public void testStatusInDelivery() throws Exception {
        String orderWaitingForPayment = "234269820000";
        String orderAllowedForPicking = "234269820001";
        String orderShippedHold = "234269820002";
        String orderShippedPaid = "234269820003";
        String orderOnDelivery = "234269820004";
        // Ожидает оплату
        createStub(PredicateType.DEEP_EQUALS, buildGetSingleDocSearchOrderRequest(orderWaitingForPayment), 0);
        createStub(PredicateType.DEEP_EQUALS, buildOrderGetRequest(orderWaitingForPayment), 1);

        // Готов к сборке
        createStub(PredicateType.DEEP_EQUALS, buildGetSingleDocSearchOrderRequest(orderAllowedForPicking), 2);
        createStub(PredicateType.DEEP_EQUALS, buildOrderGetRequest(orderAllowedForPicking), 3);

        // Отгружен
        createStub(PredicateType.DEEP_EQUALS, buildGetSingleDocSearchOrderRequest(orderShippedHold), 4);
        createStub(PredicateType.DEEP_EQUALS, buildOrderGetRequest(orderShippedHold), 5);

        // Оплачен
        createStub(PredicateType.DEEP_EQUALS, buildGetSingleDocSearchOrderRequest(orderShippedPaid), 6);
        createStub(PredicateType.DEEP_EQUALS, buildOrderGetRequest(orderShippedPaid), 7);

        // В доставке
        createStub(PredicateType.DEEP_EQUALS, buildGetSingleDocSearchOrderRequest(orderOnDelivery), 8);
        createStub(PredicateType.DEEP_EQUALS, buildOrderGetRequest(orderOnDelivery), 9);


        OrderHeaderPage orderHeaderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);

        step("Проверяем заказ в статусе 'Ожидает оплату'");
        verifyOrderStatus(orderHeaderPage, orderWaitingForPayment,
                SalesDocumentsConst.States.WAITING_FOR_PAYMENT.getUiVal(), notPaidStatus);

        step("Проверяем заказ в статусе 'Готов к сборке'");
        verifyOrderStatus(orderHeaderPage, orderAllowedForPicking,
                SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal(), holdStatus);

        step("Проверяем заказ в статусе 'Отгружен'");
        verifyOrderStatus(orderHeaderPage, orderShippedHold, "Отгружен: ожидает доставку", holdStatus);

        step("Проверяем заказ в статусе 'Оплачен'");
        verifyOrderStatus(orderHeaderPage, orderShippedPaid, "Отгружен: ожидает доставку", paidStatus);

        step("Проверяем заказ в статусе 'В доставке'");
        verifyOrderStatus(orderHeaderPage, orderOnDelivery, SalesDocumentsConst.States.ON_DELIVERY.getUiVal(),
                paidStatus);
    }

    @Test(description = "C23399960 Конечный статус 'Доставлен'")
    public void testStatusDelivered() throws Exception {
        String orderDelivered = "233999600000";

        createStub(PredicateType.DEEP_EQUALS, buildGetSingleDocSearchOrderRequest(orderDelivered), 0);
        createStub(PredicateType.DEEP_EQUALS, buildOrderGetRequest(orderDelivered), 1);

        OrderHeaderPage orderHeaderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);

        step("Проверяем заказ в статусе 'Доставлен'");
        verifyOrderStatus(orderHeaderPage, orderDelivered,
                SalesDocumentsConst.States.DELIVERED.getUiVal(), paidStatus);
    }

    @Test(description = "C23399961 Конечный статус 'Частичная доставка'")
    public void testStatusPartiallyDelivered() throws Exception {
        String orderId = "233999610000";

        createStub(PredicateType.DEEP_EQUALS, buildGetSingleDocSearchOrderRequest(orderId), 0);
        createStub(PredicateType.DEEP_EQUALS, buildOrderGetRequest(orderId), 1);

        OrderHeaderPage orderHeaderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);

        step("Проверяем заказ в статусе 'Частичная доставка'");
        verifyOrderStatus(orderHeaderPage, orderId,
                SalesDocumentsConst.States.PARTIALLY_DELIVERED.getUiVal(), paidStatus);
    }

    @Test(description = "C23399963 Статус 'Доставлен без сборки'")
    public void testStatusDeliveredWithoutPicking() throws Exception {
        String orderId = "233999630000";

        createStub(PredicateType.DEEP_EQUALS, buildGetSingleDocSearchOrderRequest(orderId), 0);
        createStub(PredicateType.DEEP_EQUALS, buildOrderGetRequest(orderId), 1);

        OrderHeaderPage orderHeaderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);

        step("Проверяем заказ в статусе 'Доставлен без сборки'");
        verifyOrderStatus(orderHeaderPage, orderId,
                SalesDocumentsConst.States.DELIVERED_WITHOUT_ASSEMBLY.getUiVal(), paidStatus);
    }

    @Test(description = "C23399962 Конечный статус 'Отказ при доставке'")
    public void testStatusCancelledOnDelivery() throws Exception {
        String orderId = "233999620000";

        createStub(PredicateType.DEEP_EQUALS, buildGetSingleDocSearchOrderRequest(orderId), 0);
        createStub(PredicateType.DEEP_EQUALS, buildOrderGetRequest(orderId), 1);

        OrderHeaderPage orderHeaderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);

        step("Проверяем заказ в статусе 'Отказ при доставке'");
        verifyOrderStatus(orderHeaderPage, orderId,
                SalesDocumentsConst.States.CANCELLED_BY_CUSTOMER_ON_DELIVERY.getUiVal(), paidStatus);
    }

}
