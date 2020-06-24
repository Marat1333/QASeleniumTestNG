package com.leroy.magportal.ui.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.api.StatusCodes;
import com.leroy.constants.customer.CustomerConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.OrderClient;
import com.leroy.magmobile.api.data.customer.PhoneData;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.*;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.models.salesdoc.OrderDetailData;
import com.leroy.magportal.ui.models.salesdoc.ShortOrderDocWebData;
import com.leroy.magportal.ui.pages.orders.OrderHeaderPage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.leroy.magportal.ui.constants.OrderConst.DeliveryType.DELIVERY_TK;
import static com.leroy.magportal.ui.constants.OrderConst.DeliveryType.PICKUP;
import static com.leroy.magportal.ui.constants.OrderConst.Status.*;

public class OrderTest extends WebBaseSteps {

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @Step("Pre-condition: Создание заказа через API")
    private String createOrderByApi(OrderDetailData orderDetailData,
                                    boolean waitUntilIsConfirmed) throws Exception {
        UserSessionData userSessionData = getUserSessionData();
        CartClient cartClient = apiClientProvider.getCartClient();
        OrderClient orderClient = apiClientProvider.getOrderClient();

        List<CartProductOrderData> productOrderDataList = new ArrayList<>();

        if (orderDetailData.getProducts() == null) {
            // Prepare request data
            CartProductOrderData productOrderData = new CartProductOrderData(
                    apiClientProvider.getProducts(1).get(0));
            productOrderData.setQuantity(1.0);
            productOrderDataList.add(productOrderData);
        }

        // Create cart
        Response<CartData> response = cartClient.sendRequestCreate(productOrderDataList);
        CartData cartData = cartClient.assertThatIsCreatedAndGetData(response, true);

        // Create Order
        ReqOrderData reqOrderData = new ReqOrderData();
        reqOrderData.setCartId(cartData.getCartId());
        reqOrderData.setDateOfGiveAway(LocalDateTime.now());
        reqOrderData.setDocumentVersion(1);

        CartEstimateProductOrderData cardProduct = cartData.getProducts().get(0);

        ReqOrderProductData postProductData = new ReqOrderProductData();
        postProductData.setLineId(cardProduct.getLineId());
        postProductData.setLmCode(cardProduct.getLmCode());
        postProductData.setQuantity(cardProduct.getQuantity());
        postProductData.setPrice(cardProduct.getPrice());

        reqOrderData.getProducts().add(postProductData);

        Response<OrderData> orderResp = orderClient.createOrder(reqOrderData);
        OrderData orderData = orderResp.asJson();

        // Set pin-code
        String validPinCode = apiClientProvider.getValidPinCode();
        Response<JsonNode> responseSetPinCode = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        if (responseSetPinCode.getStatusCode() == StatusCodes.ST_400_BAD_REQ) {
            validPinCode = apiClientProvider.getValidPinCode();
            responseSetPinCode = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        }
        orderClient.assertThatPinCodeIsSet(responseSetPinCode);

        // Confirm order
        String customerFirstName = ParserUtil.parseFirstName(orderDetailData.getCustomer().getName());
        String customerLastName = ParserUtil.parseLastName(orderDetailData.getCustomer().getName());
        OrderCustomerData orderCustomerData = new OrderCustomerData();
        orderCustomerData.setFirstName(customerFirstName);
        orderCustomerData.setLastName(customerLastName);
        orderCustomerData.setRoles(Collections.singletonList(CustomerConst.Role.PAYER.name()));
        orderCustomerData.setType(CustomerConst.Type.PERSON.name());
        orderCustomerData.setPhone(new PhoneData(orderDetailData.getCustomer().getPhoneNumber()));

        String recipientFirstName = ParserUtil.parseFirstName(orderDetailData.getRecipient().getName());
        String recipientLastName = ParserUtil.parseLastName(orderDetailData.getRecipient().getName());
        OrderCustomerData orderRecipientData = new OrderCustomerData();
        orderRecipientData.setFirstName(recipientFirstName);
        orderRecipientData.setLastName(recipientLastName);
        orderRecipientData.setRoles(Collections.singletonList(CustomerConst.Role.RECEIVER.name()));
        orderRecipientData.setType(CustomerConst.Type.PERSON.name());
        orderRecipientData.setPhone(new PhoneData(orderDetailData.getRecipient().getPhoneNumber()));

        OrderData confirmOrderData = new OrderData();
        confirmOrderData.setPriority(SalesDocumentsConst.Priorities.HIGH.getApiVal());
        confirmOrderData.setShopId(userSessionData.getUserShopId());
        confirmOrderData.setSolutionVersion(orderData.getSolutionVersion());
        confirmOrderData.setPaymentVersion(orderData.getPaymentVersion());
        confirmOrderData.setFulfillmentVersion(orderData.getFulfillmentVersion());
        confirmOrderData.setFulfillmentTaskId(orderData.getFulfillmentTaskId());
        confirmOrderData.setPaymentTaskId(orderData.getPaymentTaskId());
        confirmOrderData.setProducts(orderData.getProducts());
        confirmOrderData.setCustomers(Arrays.asList(orderCustomerData, orderRecipientData));

        GiveAwayData giveAwayData = new GiveAwayData();
        giveAwayData.setDate(LocalDateTime.now());
        giveAwayData.setPoint(SalesDocumentsConst.GiveAwayPoints.PICKUP.getApiVal());
        giveAwayData.setShopId(Integer.valueOf(userSessionData.getUserShopId()));
        confirmOrderData.setGiveAway(giveAwayData);

        Response<OrderData> respConfirm = orderClient.confirmOrder(orderData.getOrderId(), confirmOrderData);
        orderClient.assertThatIsConfirmed(respConfirm, orderData);
        if (waitUntilIsConfirmed) {
            orderClient.waitUntilOrderHasStatusAndReturnOrderData(orderData.getOrderId(),
                    SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getApiVal());
        }
        return orderData.getOrderId();
    }

    @Issue("BACKEND_ISSUE")
    @Test(description = "C22829624 Ордерс. Фильтрация по статусу заказа")
    public void testOrderFilterByStatus() throws Exception {
        // Step 1
        step("Открыть страницу с Заказами");
        OrderHeaderPage ordersPage = loginAndGoTo(OrderHeaderPage.class);
        List<ShortOrderDocWebData> documentListFirst = ordersPage.getDocumentDataList();

        // Step 2
        step("В фильтре Статус заказа выставить значение = Создан, нажать кнопку 'Показать заказы'");
        ordersPage.selectStatusFilters(CREATED)
                .clickApplyFilters()
                .shouldDocumentListContainsOnlyWithStatuses(CREATED);

        // Step 3
        step("Деактивировать фильтр Создан, убрав чекбокс, нажать кнопку 'Показать заказы'");
        ordersPage.deselectStatusFilters(CREATED)
                .clickApplyFilters()
                .shouldDocumentListIs(documentListFirst);

        // Step 4
        step("Повторить шаг 2 для статусов Сборка, Сборка(пауза), Собран, Частично собран, Выдан, Отменен");
        String[] step4Filters = {ASSEMBLY, ASSEMBLY_PAUSE, PICKED, PICKED_PARTIALLY, ISSUED, CANCELLED};
        ordersPage.selectStatusFilters(step4Filters)
                .clickApplyFilters()
                .shouldDocumentListContainsOnlyWithStatuses(step4Filters);

        // Step 5
        step("Активировать статусы Создан, Сборка, Сборка(пауза), Собран, Частично собран, Выдан, Частично выдан, Отменен");
        String[] step5Filters = {CREATED, ASSEMBLY, ASSEMBLY_PAUSE, PICKED,
                PICKED_PARTIALLY, ISSUED, ISSUED_PARTIALLY, CANCELLED};
        ordersPage.selectStatusFilters(step5Filters)
                .clickApplyFilters()
                .shouldDocumentListContainsOnlyWithStatuses(step5Filters);

        // Step 6
        step("Нажать на кнопку 'Очистить фильтры' (изображена метла), нажать 'Показать заказы'");
        ordersPage.clearFiltersAndSubmit()
                .shouldDocumentListIs(documentListFirst);
    }

    @Test(description = "C22893768 Ордерс. Фильтрация по Способу получения")
    public void testOrderFilterByDeliveryType() throws Exception {
        // Step 1
        step("Открыть страницу с Заказами");
        OrderHeaderPage ordersPage = loginAndGoTo(OrderHeaderPage.class);
        List<ShortOrderDocWebData> documentListFirst = ordersPage.getDocumentDataList();

        // Step 2
        step("В фильтре Способ получения выставить значение = Самовывоз, нажать кнопку 'Показать заказы'");
        ordersPage.selectDeliveryTypeFilters(PICKUP)
                .clickApplyFilters()
                .shouldDocumentListContainsOnlyWithDeliveryTypes(PICKUP);

        // Step 3
        step("В фильтре Способ получения выставить значение = Доставка, нажать кнопку 'Показать заказы'");
        ordersPage.clearFiltersAndSubmit()
                .selectDeliveryTypeFilters(DELIVERY_TK)
                .clickApplyFilters()
                .shouldDocumentListContainsOnlyWithDeliveryTypes(DELIVERY_TK);

        // Step 4
        step("Нажать на кнопку 'Очистить фильтры' (изображена метла), нажать 'Показать заказы'");
        ordersPage.clearFiltersAndSubmit()
                .shouldDocumentListIs(documentListFirst);
    }

    @Issue("PUZ2-2092")
    @Test(description = "C22893769 Ордерс. Фильтрация по Номеру заказа и Номеру телефона")
    public void testOrderFilterByDocNumberAndPhoneNumber() throws Exception {
        // Pre-conditions
        SimpleCustomerData customerData = new SimpleCustomerData();
        customerData.generateRandomData();
        SimpleCustomerData recipientData = new SimpleCustomerData();
        recipientData.generateRandomData();
        SimpleCustomerData customerRecipientData = new SimpleCustomerData();
        customerRecipientData.generateRandomData();

        OrderDetailData orderData1 = new OrderDetailData();
        orderData1.setCustomer(customerData);
        orderData1.setRecipient(recipientData);

        OrderDetailData orderData2 = new OrderDetailData();
        orderData2.setCustomer(customerRecipientData);
        orderData2.setRecipient(customerRecipientData);

        String orderId_1 = createOrderByApi(orderData1, false);
        String orderId_2 = createOrderByApi(orderData2, false);

        try {
            // Step 1
            step("Открыть страницу с Заказами");
            OrderHeaderPage ordersPage = loginAndGoTo(OrderHeaderPage.class);
            int ordersCountBefore = ordersPage.getDocumentCount();

            // Step 2
            step("В фильтре поиска выставить 'Номер заказа', вбить номер из предусловия, нажать " +
                    "кнопку 'Показать заказы'");
            ordersPage.selectSearchType(OrderHeaderPage.SearchTypes.ORDER_NUMBER);
            ordersPage.enterSearchTextAndSubmit(orderId_1);
            ordersPage.shouldDocumentListNumbersEqual(Collections.singletonList(orderId_1));

            // Step 3
            step("Нажать на кнопку 'Очистить фильтры'(изображена метла), нажать 'Показать заказы'");
            ordersPage.clearFiltersAndSubmit();
            ordersPage.shouldDocumentCountIs(ordersCountBefore);

            // Step 4
            step("В фильтре поиска выставить последние 4 цифры заказа из предусловия, нажать кнопку" +
                    " 'Показать заказы'");
            String partOrder = orderId_1.substring(orderId_1.length() - 4);
            ordersPage.enterSearchTextAndSubmit(partOrder);
            ordersPage.shouldDocumentListFilteredByNumber(partOrder);

            // Step 5
            step("В фильтре поиска вбить несуществующий номер заказа, напр. 9999 9999 9999");
            ordersPage.enterSearchTextAndSubmit("999999999999");
            ordersPage.shouldDocumentListIsEmpty();

            // Step 6
            step("В фильтре поиска выставить 'Номер телефона' , вбить в маске только 3 цифры, " +
                    "например '937', нажать кнопку 'Показать заказы'");
            ordersPage.selectSearchType(OrderHeaderPage.SearchTypes.PHONE_NUMBER);
            ordersPage.enterSearchTextAndSubmit("937");
            ordersPage.shouldDocumentListIsEmpty();
            // TODO - ошибка не появляется. Это баг?

            // Step 7
            step("Вбить номер телефона клиента из предусловия п.1");
            ordersPage.enterSearchTextAndSubmit(customerData.getPhoneNumber());
            ordersPage.shouldDocumentIsPresent(orderId_1);

            // Step 8
            step("Нажать на кнопку 'Очистить фильтры'(изображена метла), нажать 'Показать заказы'");
            ordersPage.clearFiltersAndSubmit();
            ordersPage.shouldDocumentCountIs(ordersCountBefore);

            // Step 9
            step("Вбить номер телефона получателя из предусловия п.1");
            ordersPage.selectSearchType(OrderHeaderPage.SearchTypes.PHONE_NUMBER);
            ordersPage.enterSearchTextAndSubmit(recipientData.getPhoneNumber());
            ordersPage.shouldDocumentIsPresent(orderId_1);

            // Step 10
            step("Вбить номер телефона клиента или получателя " +
                    "(должны совпадать) из предусловия п.2, нажать 'Показать заказы'");
            ordersPage.enterSearchTextAndSubmit(customerRecipientData.getPhoneNumber());
            ordersPage.shouldDocumentIsPresent(orderId_2);
        } finally {
            OrderClient orderClient = apiClientProvider.getOrderClient();
            orderClient.cancelOrder(orderId_1);
            orderClient.cancelOrder(orderId_2);
        }

    }

    @Test(description = "C22893767 Ордерс. Фильтрация по Дате")
    public void testOrderFilterByDate() throws Exception {
        // Step 1
        step("Открыть страницу с Заказами");
        OrderHeaderPage ordersPage = loginAndGoTo(OrderHeaderPage.class);
        int ordersCountBefore = ordersPage.getDocumentCount();

        // Step 2
        step("В фильтре 'Дата' выставить дату = От - сегодня, До - сегодня, " +
                "нажать кнопку 'Показать заказы'");
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now();
        ordersPage.selectDateCreationsFilters(fromDate, toDate)
                .clickApplyFilters();
        ordersPage.shouldDocumentListFilteredByDates(fromDate, toDate);

        // Step 3
        step("Нажать на кнопку 'Очистить фильтры'(изображена метла), нажать 'Показать заказы'");
        ordersPage.clearFiltersAndSubmit();
        ordersPage.shouldDocumentCountIs(ordersCountBefore);

        // Step 4
        step("В фильтре 'Дата' выставить дату = От - завтра, До - завтра, нажать кнопку 'Показать заказы'");
        fromDate = LocalDate.now().plusDays(1);
        toDate = LocalDate.now().plusDays(1);
        ordersPage.selectDateCreationsFilters(fromDate, toDate)
                .clickApplyFilters();
        ordersPage.shouldDocumentListIsEmpty();

        // Step 5
        step("Нажать крестик в фильтре");
        ordersPage.clearDateCreationsFilters();
        ordersPage.shouldCreationDateFilterIs(null, null);

        // Step 6
        step("В фильтре 'Дата' выставить дату = От - сегодня, До - вчера, нажать кнопку 'Показать заказы'");
        fromDate = LocalDate.now();
        toDate = LocalDate.now().minusDays(1);
        ordersPage.selectDateCreationsFilters(fromDate, toDate);
        ordersPage.shouldCreationDateFilterIs(toDate, fromDate);
        ordersPage.clickApplyFilters();
        ordersPage.shouldDocumentListFilteredByDates(toDate, fromDate);

        // Step 7
        step("Выставить в фильтре 'Дата' диапазон = неделя, нажать кнопку 'Показать заказы'");
        fromDate = LocalDate.now().minusDays(3);
        toDate = LocalDate.now().plusDays(4);
        ordersPage.selectDateCreationsFilters(fromDate, toDate)
                .clickApplyFilters();
        ordersPage.shouldDocumentListFilteredByDates(fromDate, toDate);

        // Step 8
        step("Нажать на кнопку 'Очистить фильтры'(изображена метла), нажать 'Показать заказы'");
        ordersPage.clearFiltersAndSubmit();
        ordersPage.shouldDocumentCountIs(ordersCountBefore);
    }

}
