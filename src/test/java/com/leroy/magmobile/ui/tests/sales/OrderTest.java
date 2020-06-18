package com.leroy.magmobile.ui.tests.sales;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.ui.constants.TestDataConstants;
import com.leroy.magmobile.ui.models.MagCustomerData;
import com.leroy.magmobile.ui.models.sales.*;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.sales.*;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import com.leroy.magmobile.ui.pages.sales.orders.order.CartProcessOrder35Page;
import com.leroy.magmobile.ui.pages.sales.orders.order.ConfirmedOrderPage;
import com.leroy.magmobile.ui.pages.sales.orders.order.OrderActionWithProductCardModel;
import com.leroy.magmobile.ui.pages.sales.orders.order.ProcessOrder35Page;
import com.leroy.magmobile.ui.pages.sales.orders.order.modal.ConfirmRemoveOrderModal;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class OrderTest extends SalesBaseTest {

    private final static String NEED_PRODUCTS_GROUP = "need_products";

    private String existedPickupPinCode = "11111";

    private List<String> lmCodes;
    private SalesDocumentData salesDocumentData;

    // Страницы (экраны), участвующие в данных тестах:
    SalesDocumentsPage salesDocumentsPage;
    CartProcessOrder35Page cartProcessOrder35Page;
    ProcessOrder35Page processOrder35Page;
    ConfirmedOrderPage confirmedOrderPage;
    AddProduct35Page<CartProcessOrder35Page> draftOrderAddProduct35Page;
    AddProduct35Page<ConfirmedOrderPage> confirmedOrderAddProduct35Page;
    EditProduct35Page<CartProcessOrder35Page> draftOrderEditProduct35Page;
    EditProduct35Page<ConfirmedOrderPage> confirmedOrderEditProduct35Page;
    SearchProductPage searchProductPage;
    OrderActionWithProductCardModel<CartProcessOrder35Page> draftOrderActionWithProductCardModal;
    OrderActionWithProductCardModel<ConfirmedOrderPage> confirmedOrderActionWithProductCardModal;
    SubmittedSalesDocument35Page submittedSalesDocument35Page;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeGroups(groups = NEED_PRODUCTS_GROUP)
    private void findProducts() {
        lmCodes = apiClientProvider.getProductLmCodes(3, false, false);
    }

    @AfterMethod
    private void cancelConfirmedOrder() throws Exception {
        // Clean up
        if (salesDocumentData != null &&
                salesDocumentData.getStatus().equals(SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal())) {
            cancelOrder(salesDocumentData.getNumber());
        }
    }

    // Создание Pre-condition

    private void startFromScreenWithOrderDraftAndReturnSalesDocData(
            List<String> lmCodes,
            List<CartProductOrderData> productDataList, boolean hasDiscount, boolean returnSalesDocData) throws Exception {
        if (lmCodes != null)
            startFromScreenWithCreatedCart(lmCodes, hasDiscount);
        else if (productDataList != null)
            startFromScreenWithCreatedCart(productDataList);
        else
            startFromScreenWithCreatedCart(hasDiscount);

        Cart35Page cart35Page = new Cart35Page();
        if (returnSalesDocData)
            salesDocumentData = cart35Page.getSalesDocumentData();
        cart35Page.clickMakeSalesButton();
        processOrder35Page = new ProcessOrder35Page();
        if (returnSalesDocData)
            salesDocumentData.setNumber(processOrder35Page.getOrderNumber());
    }

    @Step("Pre-condition: Создаем подтвержденный заказ")
    private void startFromScreenWithConfirmedOrder(
            List<String> lmCodes,
            List<CartProductOrderData> productDataList, boolean returnSalesDocumentData) throws Exception {
        String orderId = createConfirmedOrder(lmCodes, productDataList);

        MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);
        SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
        salesDocumentsPage.searchForDocumentByTextAndSelectIt(orderId);
        confirmedOrderPage = new ConfirmedOrderPage();
        if (returnSalesDocumentData)
            salesDocumentData = confirmedOrderPage.getSalesDocumentData();
    }

    @Step("Pre-condition: Создаем черновик заказа")
    protected void startFromScreenWithOrderDraftWithDiscount() throws Exception {
        startFromScreenWithOrderDraftAndReturnSalesDocData(null,
                null, true, true);
    }

    protected void startFromScreenWithOrderDraft(boolean returnSalesDocData) throws Exception {
        startFromScreenWithOrderDraftAndReturnSalesDocData(
                null, null, false, returnSalesDocData);
    }

    @Step("Pre-condition: Создаем черновик заказа")
    protected void startFromScreenWithOrderDraft(
            List<String> lmCodes, List<CartProductOrderData> productDataList,
            boolean returnSalesDocData) throws Exception {
        startFromScreenWithOrderDraftAndReturnSalesDocData(lmCodes, productDataList, false, returnSalesDocData);
    }

    // Подтвержденный заказ
    protected void startFromScreenWithConfirmedOrder(List<String> lmCodes) throws Exception {
        startFromScreenWithConfirmedOrder(lmCodes, null, true);
    }

    protected void startFromScreenWithConfirmedOrder() throws Exception {
        startFromScreenWithConfirmedOrder(null, null, true);
    }

    @Test(description = "C22797112 Создать заказ из корзины с одним заказом")
    public void testCreateOrderFromCartWithOneOrder() throws Exception {
        startFromScreenWithCreatedCart();

        Cart35Page cart35Page = new Cart35Page();
        double totalPrice = cart35Page.getTotalPrice();

        // Step
        step("Нажмите на кнопку Оформить");
        ProcessOrder35Page processOrder35Page = cart35Page.clickMakeSalesButton();

        // Step 2, 3, 4, 5, 6, 7
        step("Введите имя и фамилию нового пользователя, Введите PIN-код для оплаты");
        OrderDetailsData orderDetailsData = new OrderDetailsData().setRequiredRandomData();
        orderDetailsData.setPinCode(existedPickupPinCode); // Чтобы быть уверенным, что данный пин код используется
        // Данный тест все равно подберет несипользуемый ПИН
        orderDetailsData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
        processOrder35Page.fillInFormFields(orderDetailsData)
                .shouldFormFieldsAre(orderDetailsData);

        // Step 8
        step("Нажмите на кнопку Подтвердить заказ");
        SubmittedSalesDocument35Page submittedDocument35Page = processOrder35Page.clickSubmitButton()
                .verifyRequiredElements(SalesDocumentsConst.GiveAwayPoints.PICKUP)
                .shouldPinCodeIs(orderDetailsData.getPinCode());
        String documentNumber = submittedDocument35Page.getDocumentNumber();

        // Step 9
        step("Нажмите на Перейти в список документов");
        ShortSalesDocumentData expectedSalesDocument = new ShortSalesDocumentData();
        expectedSalesDocument.setDocumentTotalPrice(totalPrice);
        expectedSalesDocument.setDocumentState(SalesDocumentsConst.States.IN_PROGRESS.getUiVal());
        expectedSalesDocument.setTitle(orderDetailsData.getDeliveryType().getUiVal());
        expectedSalesDocument.setNumber(documentNumber);
        SalesDocumentsPage salesDocumentsPage = submittedDocument35Page.clickGoToDocumentListButton();
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
        ProcessOrder35Page processOrder35Page = cart35Page.clickMakeSalesButton(1);

        // Steps 2 - 7
        step("Введите имя и фамилию нового пользователя, Введите PIN-код для оплаты");
        OrderDetailsData orderDetailsData = new OrderDetailsData().setRequiredRandomData();
        orderDetailsData.setPinCode(getValidPinCode(true));
        orderDetailsData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
        processOrder35Page.fillInFormFields(orderDetailsData)
                .shouldFormFieldsAre(orderDetailsData);

        // Step 8
        step("Нажмите на кнопку Подтвердить заказ");
        SubmittedSalesDocument35Page submittedDocument35Page = processOrder35Page.clickSubmitButton()
                .verifyRequiredElements(SalesDocumentsConst.GiveAwayPoints.PICKUP, true)
                .shouldPinCodeIs(orderDetailsData.getPinCode());
        String documentNumber = submittedDocument35Page.getDocumentNumber();

        // Step 9
        step("Нажмите на Перейти в корзину");
        cart35Page = submittedDocument35Page.clickGoToCartButton();
        salesDocumentData.removeOrder(0);
        cart35Page.shouldSalesDocumentDataIs(salesDocumentData);

        // Clean up
        step("(Доп шаг) Отменяем заказ через API запрос");
        cancelOrder(documentNumber);
    }

    @Test(description = "C22797121 Ввод существующего пин кода")
    public void testEnterExistedPinCode() throws Exception {
        startFromScreenWithOrderDraft(true);
        ProcessOrder35Page processOrder35Page = new ProcessOrder35Page();

        // Steps 1
        step("Введите существующий PIN-код в соответствующее поле");
        OrderDetailsData orderDetailsData = new OrderDetailsData();
        orderDetailsData.setPinCode(existedPickupPinCode);
        processOrder35Page.enterPinCode(orderDetailsData, false);
        processOrder35Page.shouldErrorPinCodeTooltipVisible();
    }

    @Test(description = "C22797114 Подтвердить заказ на самовывоз сегодня")
    public void testConfirmOrderAsPickupToday() throws Exception {
        // Test Data
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;

        if (isStartFromScratch()) {
            startFromScreenWithOrderDraft(true);
        }

        ProcessOrder35Page processOrder35Page = new ProcessOrder35Page();

        // Steps 1
        step("В поле Выбери способ получения нажмите на кнопу Самовывоз (по умолчанию)");
        OrderDetailsData orderDetailsData = new OrderDetailsData();
        orderDetailsData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
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
                .verifyRequiredElements(SalesDocumentsConst.GiveAwayPoints.PICKUP)
                .shouldPinCodeIs(orderDetailsData.getPinCode());
        String documentNumber = submittedDocument35Page.getDocumentNumber();

        // Step 8
        step("Нажмите на Перейти в список документов");
        ShortSalesDocumentData documentData = new ShortSalesDocumentData();
        documentData.setNumber(documentNumber);
        documentData.setDocumentState(SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        documentData.setTitle(SalesDocumentsConst.GiveAwayPoints.PICKUP.getUiVal());
        documentData.setPin(orderDetailsData.getPinCode());
        documentData.setCustomerName(customerData.getName());
        documentData.setDocumentTotalPrice(salesDocumentData.getOrderAppDataList().get(0).getTotalPrice());

        SalesDocumentsPage salesDocumentsPage = submittedDocument35Page.clickGoToDocumentListButton();
        salesDocumentsPage.waitUntilDocumentIsInCorrectStatus(documentNumber, documentData.getDocumentState())
                .shouldSalesDocumentIsPresentAndDataMatches(documentData);

        // Step 9
        step("Нажать на мини-карточку созданного документа Самовывоз");
        salesDocumentData.setFieldsFrom(documentData);
        salesDocumentsPage.searchForDocumentByTextAndSelectIt(documentData.getNumber());
        new ConfirmedOrderPage()
                .shouldSalesDocumentDataIs(salesDocumentData)
                .shouldFormFieldsAre(orderDetailsData);

        // Clean up
        step("(Доп шаг) Отменяем заказ через API запрос");
        cancelOrder(documentNumber);
    }

    @Test(description = "C22797115 Подтвердить заказ на доставку на завтра")
    public void testConfirmOrderAsDeliveryTomorrow() throws Exception {
        // Test Data
        MagCustomerData customerData = TestDataConstants.CUSTOMER_WITH_SERVICE_CARD;

        // Pre-conditions
        startFromScreenWithOrderDraft(true);

        // Step 1
        step("В поле Выбери способ получения нажмите на кнопу Доставка");
        OrderDetailsData orderDetailsData = new OrderDetailsData();
        orderDetailsData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.DELIVERY);
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
                .verifyRequiredElements(SalesDocumentsConst.GiveAwayPoints.DELIVERY)
                .shouldPinCodeIs(orderDetailsData.getPinCode());
        String documentNumber = submittedDocument35Page.getDocumentNumber();

        // Step 8
        step("Нажмите на Перейти в список документов");
        ShortSalesDocumentData documentData = new ShortSalesDocumentData();
        documentData.setNumber(documentNumber);
        documentData.setDocumentState(SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        documentData.setTitle(SalesDocumentsConst.GiveAwayPoints.DELIVERY.getUiVal());
        documentData.setPin(orderDetailsData.getPinCode());
        documentData.setCustomerName(customerData.getName());
        documentData.setDocumentTotalPrice(salesDocumentData.getOrderAppDataList().get(0).getTotalPrice());

        SalesDocumentsPage salesDocumentsPage = submittedDocument35Page.clickGoToDocumentListButton();
        salesDocumentsPage.waitUntilDocumentIsInCorrectStatus(documentNumber, documentData.getDocumentState())
                .shouldSalesDocumentIsPresentAndDataMatches(documentData);

        // Step 9
        step("Нажать на мини-карточку созданного документа Доставка");
        salesDocumentData.setFieldsFrom(documentData);
        salesDocumentData.getOrderAppDataList().get(0).setDate(orderDetailsData.getDeliveryDate());

        salesDocumentsPage.searchForDocumentByTextAndSelectIt(documentData.getNumber());
        new ConfirmedOrderPage()
                .shouldSalesDocumentDataIs(salesDocumentData)
                .shouldFormFieldsAre(orderDetailsData);

        // Clean up
        step("(Доп шаг) Отменяем заказ через API запрос");
        cancelOrder(documentNumber);
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
        startFromScreenWithOrderDraft(null,
                Collections.singletonList(productWithNegativeBalance), true);

        // Step 1
        step("В поле Выбери способ получения нажмите на кнопу Доставка");
        OrderDetailsData orderDetailsData = new OrderDetailsData();
        orderDetailsData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.DELIVERY);
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
                .verifyRequiredElements(SalesDocumentsConst.GiveAwayPoints.DELIVERY)
                .shouldPinCodeIs(orderDetailsData.getPinCode());
        String documentNumber = submittedDocument35Page.getDocumentNumber();

        // Step 8
        step("Нажмите на Перейти в список документов");
        ShortSalesDocumentData documentData = new ShortSalesDocumentData();
        documentData.setNumber(documentNumber);
        documentData.setDocumentState(SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        documentData.setTitle(SalesDocumentsConst.GiveAwayPoints.DELIVERY.getUiVal());
        documentData.setPin(orderDetailsData.getPinCode());
        documentData.setCustomerName(customerData.getName());
        documentData.setDocumentTotalPrice(salesDocumentData.getOrderAppDataList().get(0).getTotalPrice());

        SalesDocumentsPage salesDocumentsPage = submittedDocument35Page.clickGoToDocumentListButton();
        salesDocumentsPage.waitUntilDocumentIsInCorrectStatus(documentNumber, documentData.getDocumentState())
                .shouldSalesDocumentIsPresentAndDataMatches(documentData);

        // Step 9
        step("Нажать на мини-карточку созданного документа Доставка");
        salesDocumentData.setFieldsFrom(documentData);
        salesDocumentData.getOrderAppDataList().get(0).setDate(orderDetailsData.getDeliveryDate());

        salesDocumentsPage.searchForDocumentByTextAndSelectIt(documentData.getNumber());
        new ConfirmedOrderPage()
                .shouldSalesDocumentDataIs(salesDocumentData)
                .shouldFormFieldsAre(orderDetailsData);

        // Clean up
        step("(Доп шаг) Отменяем заказ через API запрос");
        cancelOrder(documentNumber);
    }

    @Test(description = "C22797118 Создать заказ из корзины со скидкой")
    public void testCreateOrderFromCartWithDiscount() throws Exception {
        // Pre-condition + step 1
        startFromScreenWithOrderDraftWithDiscount();
        anAssert().isNotNull(salesDocumentData.getOrderAppDataList().get(0)
                        .getProductCardDataList().get(0).getDiscountPercent(),
                "У товара в корзине отсутствует скидка", "Товар в корзине должен иметь скидку");

        ProcessOrder35Page processOrder35Page = new ProcessOrder35Page();
        // Step 2
        step("Нажмите на иконку корзины в поле оформления заказа");
        CartProcessOrder35Page cartProcessOrder35Page = processOrder35Page.clickCartIcon();
        cartProcessOrder35Page.shouldSalesDocumentDataIs(salesDocumentData);

        // Step 3
        step("Нажмите на иконку оформления заказа");
        processOrder35Page = cartProcessOrder35Page.clickProcessOrderIcon();

        // Steps 4 - 12
        step("Введите ФИЛ нового клиента, телефон и Введите PIN-код для оплаты");
        OrderDetailsData orderDetailsData = new OrderDetailsData().setRequiredRandomData();
        orderDetailsData.setPinCode(getValidPinCode(true));
        orderDetailsData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
        processOrder35Page.fillInFormFields(orderDetailsData)
                .shouldFormFieldsAre(orderDetailsData);

        // Step 13
        step("Нажмите на кнопку Подтвердить заказ");
        SubmittedSalesDocument35Page submittedDocument35Page = processOrder35Page.clickSubmitButton()
                .verifyRequiredElements(SalesDocumentsConst.GiveAwayPoints.PICKUP)
                .shouldPinCodeIs(orderDetailsData.getPinCode());
        String documentNumber = submittedDocument35Page.getDocumentNumber();

        // Step 14
        step("Нажмите на Перейти в список документов");
        ShortSalesDocumentData documentData = new ShortSalesDocumentData();
        documentData.setNumber(documentNumber);
        documentData.setDocumentState(SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        documentData.setTitle(SalesDocumentsConst.GiveAwayPoints.PICKUP.getUiVal());
        documentData.setPin(orderDetailsData.getPinCode());
        documentData.setDocumentTotalPrice(salesDocumentData.getOrderAppDataList().get(0).getTotalPrice());
        SalesDocumentsPage salesDocumentsPage = submittedDocument35Page.clickGoToDocumentListButton();
        salesDocumentsPage.waitUntilDocumentIsInCorrectStatus(documentNumber, documentData.getDocumentState())
                .shouldSalesDocumentIsPresentAndDataMatches(documentData);

        // Step 15
        step("Нажать на мини-карточку созданного документа");
        salesDocumentsPage.searchForDocumentByTextAndSelectIt(documentData.getNumber());
        new ConfirmedOrderPage()
                .shouldSalesDocumentDataIs(salesDocumentData)
                .shouldFormFieldsAre(orderDetailsData);

        // Clean up
        step("(Доп шаг) Отменяем заказ через API запрос");
        cancelOrder(documentNumber);
    }

    @Test(description = "C22808291 Добавить товар в неподтвержденный заказ (количества товара достаточно)",
            groups = NEED_PRODUCTS_GROUP)
    public void testAddProductInNotConfirmedOrderWhenProductHasAvailableStock() throws Exception {
        startFromScreenWithOrderDraft(
                Collections.singletonList(lmCodes.get(0)), null, true);

        String newProductLmCode = lmCodes.get(1);

        // Step 1
        step("Нажать на иконку корзины в поле оформления заказа");
        stepClickCartIconWhenProcessOrder(false);

        // Step 2
        step("Нажмите на кнопку +Товар");
        stepClickAddProductButton();

        // Step 3
        step("Введите ЛМ код товара (количество товара достаточно)");
        stepSearchForProduct(newProductLmCode);

        // Step 4
        step("Нажмите на Добавить в заказ");
        stepAddProductInOrder(true);
    }

    @Test(description = "C22808292 Добавить товар в неподтвержденный заказ (количества товара недостаточно)")
    public void testAddProductInNotConfirmedOrderWhenProductHasNotAvailableStock() throws Exception {
        // Pre-conditions
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setHasAvailableStock(true);
        ProductItemData product1 = apiClientProvider.getProducts(1, filter).get(0);
        filter.setHasAvailableStock(false);
        ProductItemData product2 = apiClientProvider.getProducts(1, filter).get(0);

        CartProductOrderData cartProductOrderData = new CartProductOrderData(product1);
        cartProductOrderData.setQuantity(1.0);

        startFromScreenWithOrderDraft(null,
                Collections.singletonList(cartProductOrderData), true);

        String newProductLmCode = product2.getLmCode();

        // Step 1
        step("Нажать на иконку корзины в поле оформления заказа");
        stepClickCartIconWhenProcessOrder(false);

        // Step 2
        step("Нажмите на кнопку +Товар");
        stepClickAddProductButton();

        // Step 3
        step("Введите ЛМ код товара (количество товара достаточно)");
        stepSearchForProduct(newProductLmCode);

        // Step 4
        step("Нажмите на Добавить в заказ");
        stepAddProductInOrderWithEditQuantity(product2.getAvailableStock() + 10, true);
    }

    @Test(description = "C22808293 Изменить количество товара в неподтвержденном заказе")
    public void testChangeProductQuantityInNotConfirmedOrder() throws Exception {
        // Pre-conditions
        startFromScreenWithOrderDraft(true);

        // Step 1
        step("Нажать на иконку корзины в поле оформления заказа");
        stepClickCartIconWhenProcessOrder(false);

        // Step 2
        step("Нажмите на мини-карточку товара в списке доступных товаров");
        stepClickProductCard(1);

        // Step 3
        step("Выберите параметр Изменить количество");
        stepClickChangeQuantityInModalWindow();

        // Step 4 - 6
        step("Измените количество товара");
        stepChangeQuantityProduct(2);

        // Step 7
        step("Нажмите на кнопку Сохранить");
        stepSaveEditProductChanges(true);
    }

    @Test(description = "C22808294 Добавить Топ ЕМ или AVS товар в неподтвержденный заказ")
    public void testAddTopEmOrAvsProductInNotConfirmedOrder() throws Exception {
        // Pre-conditions
        startFromScreenWithOrderDraft(true);

        boolean oddDay = LocalDate.now().getDayOfMonth() % 2 == 1;
        String newProductLmCode = oddDay ? getAnyLmCodeProductWithTopEM(true) :
                getAnyLmCodeProductWithAvs(true);

        // Step 1
        step("Нажать на иконку корзины в поле оформления заказа");
        stepClickCartIconWhenProcessOrder(false);

        // Step 2
        step("Нажмите на кнопку +Товар");
        stepClickAddProductButton();

        // Step 3
        step("Введите ЛМ код товара (товар Топ ЕМ или AVS)");
        stepSearchForProduct(newProductLmCode);

        // Step 4
        step("Нажмите на Добавить в заказ");
        stepAddProductInOrder(true);
    }

    @Test(description = "C22808295 Удалить товар из неподтвержденного заказа", groups = NEED_PRODUCTS_GROUP)
    public void testRemoveProductFromNotConfirmedOrder() throws Exception {
        // Pre-conditions
        startFromScreenWithOrderDraft(lmCodes.subList(0, 3), null, true);

        // Step 1
        step("Нажать на иконку корзины в поле оформления заказа");
        stepClickCartIconWhenProcessOrder(false);

        // Step 2
        step("Нажать на мини-карточку товара из списка доступных");
        stepClickProductCard(1);

        // Step 3 and 4
        step("Выберите параметр Удалить товар и подтвердите удаление");
        stepSelectRemoveProductInModalWindow(true);
    }

    @Test(description = "C22808296 Добавить товар в неподтвержденный заказ (из модалки действий с товаром)")
    public void testAddProductInNotConfirmedOrderFromActionWithProductModal() throws Exception {
        startFromScreenWithOrderDraft(true);
        stepClickCartIconWhenProcessOrder(false);

        // Step 1
        step("Нажать на мини-карточку товара из списка доступных");
        stepClickProductCard(1);

        // Step 2
        step("Выберите параметр Добавить товар еще раз");
        stepClickAddProductAgainInModalWindow();

        // Steps 3 - 6
        step("Измените кол-во товара и добавьте его ");
        stepAddProductInOrderWithEditQuantity(2.0, true);
    }

    @Test(description = "C22847244 Удалить последний товар из неподтвержденного заказа")
    public void testRemoveLastProductFromNotConfirmedOrder() throws Exception {
        // Pre-conditions
        startFromScreenWithOrderDraft(true);

        // Step 1
        step("Нажать на иконку корзины в поле оформления заказа");
        stepClickCartIconWhenProcessOrder(false);

        // Step 2
        step("Нажать на мини-карточку товара из списка доступных");
        stepClickProductCard(1);

        // Step 3 and 4
        step("Выберите параметр Удалить товар и подтвердите удаление");
        stepRemoveLastProductAndRemoveOrder(true);
    }

    @Test(description = "C22847242 Удалить неподтвержденный заказ")
    public void testRemoveNotConfirmedOrder() throws Exception {
        startFromScreenWithOrderDraft(true);

        // Step 1 and 2
        step("В правом верхнем углу нажмите на кнопку удаления заказа");
        stepRemoveOrder(true);
    }

    @Test(description = "C22847029 Добавить товар в подтвержденный заказ", groups = NEED_PRODUCTS_GROUP)
    public void testAddProductInConfirmedOrder() throws Exception {
        startFromScreenWithConfirmedOrder(lmCodes.subList(0, 1));

        // Step 1
        step("Нажмите на кнопку +Товар");
        stepClickAddProductButton();

        // Step 2
        step("Введите ЛМ код товара");
        stepSearchForProduct(lmCodes.get(1));

        // Step 3
        step("Нажмите на Добавить в заказ");
        stepAddProductInOrder(true);

        // Step 4
        step("Нажмите на кнопку Сохранить");
        stepClickSaveButtonForChangesConfirmedOrder();

        // Step 5
        step("Нажмите на Перейти в список документов");
        stepClickGoToSalesDocumentsList(true);

        // Step 6
        step("Нажмите на мини-карточку созданного документа");
        stepClickSalesDocumentCard(true);
    }

    @Issue("NEW_BUG")
    @Test(description = "C22847030 Изменить количество товара в подтвержденном заказе")
    public void testChangeQuantityProductInConfirmedOrder() throws Exception {
        startFromScreenWithConfirmedOrder();
        anAssert().isTrue(salesDocumentData.getOrderAppDataList().get(0)
                        .getProductCardDataList().get(0).getSelectedQuantity() > 1,
                "Для того, чтобы тест был валиден. Штук товара в корзине должно быть больше 1-ого");

        // Step 1
        step("Нажмите на мини-карточку товара в списке доступных товаров");
        stepClickProductCard(1);

        // Step 2
        step("Выберите параметр Изменить количество");
        stepClickChangeQuantityInModalWindow();

        // Step 3 - 5
        step("Измените количество товара");
        stepChangeQuantityProduct(1);

        // Step 6
        step("Нажмите на кнопку Сохранить");
        stepSaveEditProductChanges(true);

        // Step 7
        step("Нажмите на кнопку Сохранить");
        stepClickSaveButtonForChangesConfirmedOrder();

        // Step 8
        step("Нажмите на Перейти в список документов");
        stepClickGoToSalesDocumentsList(true);

        // Step 9
        step("Нажмите на мини-карточку созданного документа");
        stepClickSalesDocumentCard(true);
    }

    @Test(description = "C22847031 Удалить товар из подтвержденного заказа", groups = NEED_PRODUCTS_GROUP)
    public void testRemoveProductFromConfirmedOrder() throws Exception {
        startFromScreenWithConfirmedOrder(lmCodes.subList(0, 2));
        OrderAppData orderAppData = salesDocumentData.getOrderAppDataList().get(0);
        // Т.к. в списке документов стоимость может отличаться от стоимости при открытии карточки,
        // приходиться делать такие вот "костыли"
        double totalPriceBefore = orderAppData.getTotalPrice();

        // Step 1
        step("Нажать на мини-карточку последнего добавленного товара из списка доступных");
        stepClickProductCard(1);

        // Step 2
        step("Выберите параметр Удалить товар");
        stepSelectRemoveProductInModalWindow(true);

        // Step 3
        step("Нажмите на кнопку Сохранить");
        stepClickSaveButtonForChangesConfirmedOrder();

        // Step 4
        step("Нажмите на Перейти в список документов");
        double totalPriceAfter = orderAppData.getTotalPrice();
        orderAppData.setTotalPrice(totalPriceBefore);
        stepClickGoToSalesDocumentsList(true);
        orderAppData.setTotalPrice(totalPriceAfter);

        // Step 5
        step("Нажмите на мини-карточку оформленного документа");
        stepClickSalesDocumentCard(true);
    }

    @Test(description = "C22847245 Удалить последний товар из подтвержденного заказа")
    public void testRemoveLastProductFromConfirmedOrder() throws Exception {
        startFromScreenWithConfirmedOrder();

        // Step 1
        step("Нажать на мини-карточку последнего добавленного товара из списка доступных");
        stepClickProductCard(1);

        // Step 2
        step("Выберите параметр Удалить товар");
        stepRemoveLastProductAndRemoveOrder(true);

        // Step 3 - 4
        step("Нажмите на кнопку Сохранить и подтвердите удаление");
        stepClickSaveButtonForChangesConfirmedOrder();

        // Step 5
        step("Нажмите на мини-карточку отмененного документа");
        stepClickSalesDocumentCard(true);
    }

    @Test(description = "C22847243 Отменить подтвержденный заказ")
    public void testCancelConfirmedOrder() throws Exception {
        startFromScreenWithConfirmedOrder();

        // Step 1 - 2
        step("В правом верхнем углу нажмите на кнопку удаления заказа");
        stepRemoveOrder(true);

        // Step 3
        step("Нажмите на мини-карточку отмененного документа");
        stepClickSalesDocumentCard(true);
    }

    //   ============ Шаги тестов =================== //

    /**
     * Нажать на иконку корзины в поле оформления заказа
     */
    private void stepClickCartIconWhenProcessOrder(boolean verifyProducts) {
        step("Нажать на иконку корзины в поле оформления заказа");
        if (processOrder35Page == null)
            processOrder35Page = new ProcessOrder35Page();
        cartProcessOrder35Page = processOrder35Page.clickCartIcon();
        if (verifyProducts)
            cartProcessOrder35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

    /**
     * Нажмите на кнопку +Товар
     */
    private void stepClickAddProductButton() {
        if (confirmedOrderPage != null) {
            searchProductPage = confirmedOrderPage.clickAddProductButton();
        } else {
            searchProductPage = cartProcessOrder35Page.clickAddProductButton();
        }
        searchProductPage.verifyRequiredElements();
    }

    /**
     * Нажмите на кнопку Сохранить (После редактирования подтвержденного закона)
     */
    private void stepClickSaveButtonForChangesConfirmedOrder() throws Exception {
        if (salesDocumentData.getOrderAppDataList().get(0).getTotalPrice() == 0) {
            ConfirmRemoveOrderModal confirmRemoveOrderModal = confirmedOrderPage.clickSaveButton(
                    ConfirmRemoveOrderModal.class);
            confirmRemoveOrderModal.clickConfirmButton();

            salesDocumentData.setStatus(SalesDocumentsConst.States.CANCELLED.getUiVal());

            salesDocumentsPage = new SalesDocumentsPage();
            ShortSalesDocumentData expectedSalesDocument = new ShortSalesDocumentData();
            // Итого стоимость остается прежней и не особо важна при отмененном заказе:
            //expectedSalesDocument.setDocumentTotalPrice(salesDocumentData.getOrderAppDataList().get(0).getTotalPrice());
            expectedSalesDocument.setDocumentState(salesDocumentData.getStatus());
            expectedSalesDocument.setTitle(salesDocumentData.getTitle());
            expectedSalesDocument.setNumber(salesDocumentData.getNumber());
            expectedSalesDocument.setPin("");
            //expectedSalesDocument.setDate(salesDocumentData.getDate()); - Дата может отличаться на минуту
            if (SalesDocumentsConst.GiveAwayPoints.DELIVERY.equals(salesDocumentData.getOrderDetailsData().getDeliveryType())) {
                expectedSalesDocument.setCustomerName(salesDocumentData.getOrderDetailsData().getCustomer().getName());
            }
            salesDocumentsPage.shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument, true);
        } else {
            submittedSalesDocument35Page = confirmedOrderPage.clickSaveButton(SubmittedSalesDocument35Page.class)
                    .verifyRequiredElements(
                            salesDocumentData.getOrderDetailsData().getDeliveryType(), false, false);
            if (salesDocumentData != null) {
                submittedSalesDocument35Page.shouldPinCodeIs(salesDocumentData.getOrderDetailsData().getPinCode());
                if (salesDocumentData.getNumber() != null)
                    submittedSalesDocument35Page.shouldDocumentNumberIs(salesDocumentData.getNumber());
            }
        }
    }

    /**
     * Нажмите на мини-карточку товара в списке доступных товаров
     */
    private void stepClickProductCard(int index) throws Exception {
        salesDocumentData.setProductDataInEditModeNow(
                salesDocumentData.getOrderAppDataList().get(0).getProductCardDataList().get(index - 1));
        if (confirmedOrderPage == null) {
            draftOrderActionWithProductCardModal = cartProcessOrder35Page.clickCardByIndex(index)
                    .verifyRequiredElements();
        } else {
            confirmedOrderActionWithProductCardModal = confirmedOrderPage.clickCardByIndex(index)
                    .verifyRequiredElements();
        }
    }

    /**
     * Выберите параметр Удалить товар в модальном окне и подтвердите удаление
     */
    private void stepSelectRemoveProductInModalWindow(boolean verifyProducts) throws Exception {
        if (confirmedOrderActionWithProductCardModal != null) {
            confirmedOrderActionWithProductCardModal.clickRemoveProductMenuItem();
            int productCount = salesDocumentData.getOrderAppDataList().get(0).getProductCount();
            OrderAppData orderAppData = salesDocumentData.getOrderDataInEditModeNow();
            orderAppData.removeProduct(salesDocumentData.getProductDataInEditModeNow());
            orderAppData.setTotalWeight(null);
            orderAppData.setProductCount(productCount); // Кол-во товаров в Confirmed заказе не изменяется
            confirmedOrderPage = new ConfirmedOrderPage();
            if (verifyProducts)
                confirmedOrderPage.shouldSalesDocumentDataIs(salesDocumentData);
        } else {
            draftOrderActionWithProductCardModal.clickRemoveProductMenuItem();
            salesDocumentData.getOrderDataInEditModeNow().removeProduct(salesDocumentData.getProductDataInEditModeNow());
            salesDocumentData.getOrderAppDataList().get(0).setTotalWeight(null);
            cartProcessOrder35Page = new CartProcessOrder35Page();
            cartProcessOrder35Page.waitUntilTotalOrderPriceIs(salesDocumentData.getOrderAppDataList().get(0).getTotalPrice());
            if (verifyProducts)
                cartProcessOrder35Page.shouldSalesDocumentDataIs(salesDocumentData);
        }
    }

    /**
     * Удалить заказ, нажав на иконку удаления (мусорка)
     */
    private void stepRemoveOrder(boolean checkThatOrderIsDeleted) {
        if (confirmedOrderPage != null) {
            confirmedOrderPage.clickTrashIcon()
                    .clickConfirmButton();
            salesDocumentsPage = new SalesDocumentsPage();
            salesDocumentData.setStatus(SalesDocumentsConst.States.CANCELLED.getUiVal());
            OrderAppData orderAppData = salesDocumentData.getOrderAppDataList().get(0);
            orderAppData.setTotalWeight(0.0);
            int productCountBefore = orderAppData.getProductCount();
            double totalPriceBefore = orderAppData.getTotalPrice();
            for (int i = 0; i < orderAppData.getProductCardDataList().size(); i++) {
                orderAppData.removeProduct(i);
            }
            orderAppData.setProductCount(productCountBefore);
            if (checkThatOrderIsDeleted) {
                ShortSalesDocumentData expectedSalesDocument = new ShortSalesDocumentData();
                expectedSalesDocument.setDocumentTotalPrice(totalPriceBefore);
                expectedSalesDocument.setDocumentState(salesDocumentData.getStatus());
                expectedSalesDocument.setTitle(salesDocumentData.getTitle());
                expectedSalesDocument.setNumber(salesDocumentData.getNumber());
                expectedSalesDocument.setPin("");
                //expectedSalesDocument.setDate(salesDocumentData.getDate()); - Дата может отличаться на минуту
                if (SalesDocumentsConst.GiveAwayPoints.DELIVERY.equals(salesDocumentData.getOrderDetailsData().getDeliveryType())) {
                    expectedSalesDocument.setCustomerName(salesDocumentData.getOrderDetailsData().getCustomer().getName());
                }
                salesDocumentsPage.shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument, true);
            }
            orderAppData.setTotalPrice(0.0);
        } else {
            processOrder35Page.clickTrashIcon()
                    .verifyRequiredElements().clickConfirmButton();
            if (checkThatOrderIsDeleted)
                new SalesDocumentsPage().shouldSalesDocumentIsNotPresent(salesDocumentData.getNumber());
        }
    }

    /**
     * Удалить последний товар из заказа (удалить заказ)
     */
    private void stepRemoveLastProductAndRemoveOrder(boolean verification) throws Exception {
        if (confirmedOrderActionWithProductCardModal != null) {
            confirmedOrderActionWithProductCardModal.clickRemoveProductMenuItem();
            confirmedOrderPage = new ConfirmedOrderPage();

            OrderAppData orderAppData = salesDocumentData.getOrderDataInEditModeNow();
            orderAppData.removeProduct(salesDocumentData.getProductDataInEditModeNow());
            orderAppData.setTotalWeight(0.0);
            orderAppData.setProductCount(1);

            if (verification)
                confirmedOrderPage.shouldSalesDocumentDataIs(salesDocumentData);
        } else {
            draftOrderActionWithProductCardModal.clickRemoveProductMenuItem();
            ConfirmRemoveOrderModal modal = new ConfirmRemoveOrderModal();
            modal.verifyRequiredElements();
            modal.clickConfirmButton();
            if (verification)
                new SalesDocumentsPage().shouldSalesDocumentIsNotPresent(salesDocumentData.getNumber());
        }
    }

    /**
     * Выберите параметр Изменить количество в модальном окне
     */
    private void stepClickChangeQuantityInModalWindow() {
        if (draftOrderActionWithProductCardModal != null) {
            draftOrderEditProduct35Page = draftOrderActionWithProductCardModal.clickChangeQuantityMenuItem()
                    .verifyRequiredElements();
        } else {
            confirmedOrderEditProduct35Page = confirmedOrderActionWithProductCardModal.clickChangeQuantityMenuItem()
                    .verifyRequiredElements();
        }
    }

    /**
     * Выберите параметр Добавить товар еще раз в модальном окне
     */
    private void stepClickAddProductAgainInModalWindow() {
        draftOrderAddProduct35Page = draftOrderActionWithProductCardModal.clickAddProductAgainMenuItem()
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_ORDER);
    }

    /**
     * Нажмите на кнопку Сохранить на экране редактирования товара
     */
    private void stepSaveEditProductChanges(boolean verifyChanges) throws Exception {
        if (draftOrderEditProduct35Page != null) {
            cartProcessOrder35Page = draftOrderEditProduct35Page.clickSaveButton();
            cartProcessOrder35Page.waitUntilTotalOrderPriceIs(salesDocumentData.getOrderAppDataList().get(0).getTotalPrice());
            if (verifyChanges)
                cartProcessOrder35Page.shouldSalesDocumentDataIs(salesDocumentData);
        } else {
            confirmedOrderPage = confirmedOrderEditProduct35Page.clickSaveButton();
            if (verifyChanges)
                confirmedOrderPage.shouldSalesDocumentDataIs(salesDocumentData);
        }
    }

    /**
     * Введите ЛМ код товара на экране поиска товаров
     */
    private void stepSearchForProduct(String searchText) {
        searchProductPage.enterTextInSearchFieldAndSubmit(searchText);
        if (confirmedOrderPage != null) {
            confirmedOrderAddProduct35Page = new AddProduct35Page<>(ConfirmedOrderPage.class)
                    .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_ORDER);
        } else {
            draftOrderAddProduct35Page = new AddProduct35Page<>(CartProcessOrder35Page.class)
                    .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_ORDER);
        }
    }

    /**
     * Изменить кол-во товара и Добавить товар в заказ
     */
    private void stepAddProductInOrderWithEditQuantity(Double quantity, boolean verifyProducts) throws Exception {
        boolean isConfirmedOrder = draftOrderAddProduct35Page == null;
        AddProduct35Page page = isConfirmedOrder ? confirmedOrderAddProduct35Page : draftOrderAddProduct35Page;
        ProductOrderCardAppData productData = page.getProductOrderDataFromPage();
        productData.setAvailableTodayQuantity(null);

        if (quantity != null) {
            productData.setSelectedQuantity(quantity);
            productData.setTotalPrice(ParserUtil.multiply(quantity, productData.getPrice(), 2));
            page.enterQuantityOfProduct((int) Math.round(quantity), false);
        }

        salesDocumentData.getOrderAppDataList().get(0).addFirstProduct(productData);
        salesDocumentData.getOrderAppDataList().get(0).setTotalWeight(null);

        if (isConfirmedOrder) {
            confirmedOrderPage = confirmedOrderAddProduct35Page.clickAddIntoOrderButton();
        } else {
            cartProcessOrder35Page = draftOrderAddProduct35Page.clickAddIntoOrderButton();
        }
        if (verifyProducts)
            if (isConfirmedOrder)
                confirmedOrderPage.shouldSalesDocumentDataIs(salesDocumentData);
            else
                cartProcessOrder35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

    /**
     * Добавить товар в заказ
     */
    private void stepAddProductInOrder(boolean verifyProducts) throws Exception {
        stepAddProductInOrderWithEditQuantity(null, verifyProducts);
    }

    /**
     * Измените количество товара на странице для редактирования товара
     */
    private void stepChangeQuantityProduct(int quantity) {
        OrderAppData curOrder = salesDocumentData.getOrderDataInEditModeNow();
        curOrder.changeProductQuantity(salesDocumentData.getProductDataInEditModeNow(), quantity);
        curOrder.setTotalWeight(null);
        if (draftOrderEditProduct35Page != null)
            draftOrderEditProduct35Page.enterQuantityOfProduct(quantity, true);
        else
            confirmedOrderEditProduct35Page.enterQuantityOfProduct(quantity, true);
    }

    /**
     * Нажмите на Перейти в список документов
     */
    private void stepClickGoToSalesDocumentsList(boolean verifyDocumentDataMatches) {
        salesDocumentsPage = submittedSalesDocument35Page.clickGoToDocumentListButton();
        if (verifyDocumentDataMatches) {
            ShortSalesDocumentData expectedSalesDocument = new ShortSalesDocumentData();
            expectedSalesDocument.setDocumentTotalPrice(salesDocumentData.getOrderAppDataList().get(0).getTotalPrice());
            expectedSalesDocument.setDocumentState(salesDocumentData.getStatus());
            expectedSalesDocument.setTitle(salesDocumentData.getTitle());
            expectedSalesDocument.setNumber(salesDocumentData.getNumber());
            expectedSalesDocument.setPin(salesDocumentData.getOrderDetailsData().getPinCode());
            //expectedSalesDocument.setDate(salesDocumentData.getDate()); - Дата может отличаться на минуту
            if (SalesDocumentsConst.GiveAwayPoints.DELIVERY.equals(salesDocumentData.getOrderDetailsData().getDeliveryType())) {
                expectedSalesDocument.setCustomerName(salesDocumentData.getOrderDetailsData().getCustomer().getName());
            }
            salesDocumentsPage.shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);
        }
    }

    /**
     * Нажмите на мини-карточку созданного документа.
     */
    private void stepClickSalesDocumentCard(boolean verifyProducts) {
        salesDocumentsPage.searchForDocumentByTextAndSelectIt(salesDocumentData.getNumber());
        if (confirmedOrderPage != null)
            confirmedOrderPage = new ConfirmedOrderPage();
        if (verifyProducts) {
            confirmedOrderPage.shouldSalesDocumentDataIs(salesDocumentData);
            if (SalesDocumentsConst.States.CANCELLED.getUiVal().equals(salesDocumentData.getStatus())) {
                confirmedOrderPage.shouldAllActiveButtonsAreDisabled();
            }
        }
    }


}
