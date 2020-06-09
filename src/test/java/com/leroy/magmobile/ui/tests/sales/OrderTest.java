package com.leroy.magmobile.ui.tests.sales;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.ui.constants.TestDataConstants;
import com.leroy.magmobile.ui.models.MagCustomerData;
import com.leroy.magmobile.ui.models.sales.OrderDetailsData;
import com.leroy.magmobile.ui.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.models.sales.ShortSalesDocumentData;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SubmittedSalesDocument35Page;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import com.leroy.magmobile.ui.pages.sales.orders.order.ProcessOrder35Page;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class OrderTest extends SalesBaseTest {

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @Test(description = "C22797112 Создать заказ из корзины с одним заказом")
    public void testCreateOrderFromCartWithOneOrder() throws Exception {
        startFromScreenWithCreatedCart();

        Cart35Page cart35Page = new Cart35Page();
        double totalPrice = cart35Page.getTotalPrice();

        // Step
        step("Нажмите на кнопку Оформить");
        ProcessOrder35Page processOrder35Page = cart35Page.clickMakeSalesButton()
                .verifyRequiredElements();

        // Step 2, 3, 4, 5, 6, 7
        step("Введите имя и фамилию нового пользователя, Введите PIN-код для оплаты");
        OrderDetailsData orderDetailsData = new OrderDetailsData().setRequiredRandomData();
        orderDetailsData.setPinCode(getValidPinCode(true));
        orderDetailsData.setDeliveryType(OrderDetailsData.DeliveryType.PICKUP);
        processOrder35Page.fillInFormFields(orderDetailsData)
                .shouldFormFieldsAre(orderDetailsData);

        // Step 8
        step("Нажмите на кнопку Подтвердить заказ");
        SubmittedSalesDocument35Page submittedDocument35Page = processOrder35Page.clickSubmitButton()
                .verifyRequiredElements()
                .shouldPinCodeIs(orderDetailsData.getPinCode());
        String documentNumber = submittedDocument35Page.getDocumentNumber();

        // Step 9
        step("Нажмите на Перейти в список документов");
        ShortSalesDocumentData expectedSalesDocument = new ShortSalesDocumentData();
        expectedSalesDocument.setDocumentTotalPrice(totalPrice);
        expectedSalesDocument.setDocumentState(SalesDocumentsConst.States.IN_PROGRESS.getUiVal());
        expectedSalesDocument.setTitle(orderDetailsData.getDeliveryType().getValue());
        expectedSalesDocument.setNumber(documentNumber);
        SalesDocumentsPage salesDocumentsPage = submittedDocument35Page.clickSubmitButton();
        salesDocumentsPage.shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);

        // Clean up
        step("(Доп шаг) Отменяем заказ через API запрос");
        cancelOrder(documentNumber);
    }

    @Test(description = "C22797113 Создать последовательно заказы из корзины с двумя заказами")
    public void testCreateOrdersFromCartWithTwoOrders() throws Exception {
        startFromScreenWithCreatedCart(findProductsForSeveralOrdersInCart());

        Cart35Page cart35Page = new Cart35Page();
        SalesDocumentData salesDocumentData = cart35Page.getSalesDocumentData();

        // Step 1
        step("Нажмите на кнопку Оформить Заказ №1");
        ProcessOrder35Page processOrder35Page = cart35Page.clickMakeSalesButton(1)
                .verifyRequiredElements();

        // Steps 2 - 7
        step("Введите имя и фамилию нового пользователя, Введите PIN-код для оплаты");
        OrderDetailsData orderDetailsData = new OrderDetailsData().setRequiredRandomData();
        orderDetailsData.setPinCode(getValidPinCode(true));
        orderDetailsData.setDeliveryType(OrderDetailsData.DeliveryType.PICKUP);
        processOrder35Page.fillInFormFields(orderDetailsData)
                .shouldFormFieldsAre(orderDetailsData);

        // Step 8
        step("Нажмите на кнопку Подтвердить заказ");
        SubmittedSalesDocument35Page submittedDocument35Page = processOrder35Page.clickSubmitButton()
                .verifyRequiredElements()
                .shouldPinCodeIs(orderDetailsData.getPinCode());
        String documentNumber = submittedDocument35Page.getDocumentNumber();

        // Step 9
        step("Нажмите на Перейти в корзину");
        // TODO

    }

    @Test(description = "C22797114 Подтвердить заказ на самовывоз сегодня")
    public void testConfirmOrderAsPickupToday() throws Exception {
        // Test Data
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;

        startFromScreenWithOrderDraft();

        ProcessOrder35Page processOrder35Page = new ProcessOrder35Page();

        // Steps 1
        step("В поле Выбери способ получения нажмите на кнопу Самовывоз (по умолчанию)");
        OrderDetailsData orderDetailsData = new OrderDetailsData();
        orderDetailsData.setDeliveryType(OrderDetailsData.DeliveryType.PICKUP);
        orderDetailsData.setDeliveryDate(LocalDate.now());
        processOrder35Page.shouldFormFieldsAre(orderDetailsData);

        // Step 2
        step("В поле Имя и Фамилия нажать на иконку клиента");
        SearchCustomerPage searchCustomerPage = processOrder35Page.clickCustomerIconToSearch()
                .verifyRequiredElements();

        // Step 3
        step("Введите номер телефона клиента и выберите нужного клиента");
        orderDetailsData.setCustomer(customerData);
        searchCustomerPage.searchCustomerByPhone(customerData.getPhone());
        processOrder35Page = new ProcessOrder35Page();
        processOrder35Page.shouldFormFieldsAre(orderDetailsData);

        // Step 4, 5, 6
        step("Введите PIN-код для оплаты (код начинается не с 9)");
        orderDetailsData.setPinCode(getValidPinCode(true));
        processOrder35Page.enterPinCode(orderDetailsData, true);
        processOrder35Page.shouldFormFieldsAre(orderDetailsData);

        // Step 7
        step("Нажмите на кнопку Подтвердить заказ");
        SubmittedSalesDocument35Page submittedDocument35Page = processOrder35Page.clickSubmitButton()
                .verifyRequiredElements(); // TODO

        // Step 8
        step("Нажмите на Перейти в список документов");

        // Step 9
        step("Нажать на мини-карточку созданного документа Самовывоз");
        // TODO

    }

    @Test(description = "C22797115 Подтвердить заказ на доставку на завтра")
    public void testConfirmOrderAsDeliveryTomorrow() throws Exception {
        // Test Data
        MagCustomerData customerData = TestDataConstants.CUSTOMER_WITH_SERVICE_CARD;

        // Pre-conditions
        startFromScreenWithOrderDraft();

        // Step 1
        step("В поле Выбери способ получения нажмите на кнопу Доставка");
        OrderDetailsData orderDetailsData = new OrderDetailsData();
        orderDetailsData.setDeliveryType(OrderDetailsData.DeliveryType.DELIVERY);
        orderDetailsData.setDeliveryDate(LocalDate.now().plusDays(1));

        ProcessOrder35Page processOrder35Page = new ProcessOrder35Page();
        processOrder35Page.selectDeliveryType(orderDetailsData.getDeliveryType());
        processOrder35Page.shouldFormFieldsAre(orderDetailsData);

        // Step 2
        step("В поле Имя и Фамилия нажать на иконку клиента");
        SearchCustomerPage searchCustomerPage = processOrder35Page.clickCustomerIconToSearch()
                .verifyRequiredElements();

        // Step 3
        step("Введите номер №карты клиента (профи) и выберите нужного клиента");
        orderDetailsData.setCustomer(customerData);
        searchCustomerPage.searchCustomerByCard(customerData.getCardNumber());
        processOrder35Page = new ProcessOrder35Page();
        processOrder35Page.shouldFormFieldsAre(orderDetailsData);

        // Step 4, 5, 6
        step("Введите PIN-код для оплаты (код начинается с 9)");
        orderDetailsData.setPinCode(getValidPinCode(false));
        processOrder35Page.enterPinCode(orderDetailsData, true);
        processOrder35Page.shouldFormFieldsAre(orderDetailsData);

        // Step 7
        step("Нажмите на кнопку Подтвердить заказ");
        SubmittedSalesDocument35Page submittedDocument35Page = processOrder35Page.clickSubmitButton()
                .verifyRequiredElements(); // TODO

        // Step 8
        step("Нажмите на Перейти в список документов");

        // Step 9
        step("Нажать на мини-карточку созданного документа Самовывоз");
        // TODO

    }

    @Test(description = "C22797116 Подтвердить заказ на самовывоз через 14 дней")
    public void testConfirmOrderAsPickupAfter14Days() throws Exception {

        // TODO

    }

    @Test(description = "C22797117 Подтвердить заказ на доставку через 15 дней")
    public void testConfirmOrderAsDeliveryAfter15Days() throws Exception {
        // Test Data
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;

        List<ProductItemData> productItemDataList = apiClientProvider.getProducts(1);
        CartProductOrderData productWithNegativeBalance = new CartProductOrderData(
                productItemDataList.get(0));
        productWithNegativeBalance.setQuantity(productItemDataList.get(0).getAvailableStock() + 10.0);

        // Pre-conditions
        startFromScreenWithOrderDraft(Collections.singletonList(productWithNegativeBalance));

        // Step 1
        step("В поле Выбери способ получения нажмите на кнопу Доставка");
        OrderDetailsData orderDetailsData = new OrderDetailsData();
        orderDetailsData.setDeliveryType(OrderDetailsData.DeliveryType.DELIVERY);
        orderDetailsData.setDeliveryDate(LocalDate.now().plusDays(15));

        ProcessOrder35Page processOrder35Page = new ProcessOrder35Page();
        processOrder35Page.selectDeliveryType(orderDetailsData.getDeliveryType());
        processOrder35Page.shouldFormFieldsAre(orderDetailsData);

        // Step 2
        step("В поле Имя и Фамилия нажать на иконку клиента");
        SearchCustomerPage searchCustomerPage = processOrder35Page.clickCustomerIconToSearch()
                .verifyRequiredElements();

        // Step 3
        step("Введите номер телефона клиента и выберите нужного клиента");
        orderDetailsData.setCustomer(customerData);
        searchCustomerPage.searchCustomerByPhone(customerData.getPhone());
        processOrder35Page = new ProcessOrder35Page();
        processOrder35Page.shouldFormFieldsAre(orderDetailsData);

        // Step 4, 5, 6
        step("Введите PIN-код для оплаты (код начинается с 9)");
        orderDetailsData.setPinCode(getValidPinCode(false));
        processOrder35Page.enterPinCode(orderDetailsData, true);
        processOrder35Page.shouldFormFieldsAre(orderDetailsData);

        // Step 7
        step("Нажмите на кнопку Подтвердить заказ");
        SubmittedSalesDocument35Page submittedDocument35Page = processOrder35Page.clickSubmitButton()
                .verifyRequiredElements(); // TODO

        // Step 8
        step("Нажмите на Перейти в список документов");

        // TODO

    }

    @Test(description = "C22797118 Создать заказ из корзины со скидкой")
    public void testCreateOrderFromCartWithDiscount() throws Exception {
        // Pre-condition + step 1
        startFromScreenWithOrderDraft(true);

        // Step 2
        step("Нажмите на иконку корзины в поле оформления заказа");

    }

}
