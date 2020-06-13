package com.leroy.magmobile.ui.tests.sales;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.ui.constants.TestDataConstants;
import com.leroy.magmobile.ui.models.MagCustomerData;
import com.leroy.magmobile.ui.models.sales.*;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.EditProduct35Page;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SubmittedSalesDocument35Page;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import com.leroy.magmobile.ui.pages.sales.orders.order.CartProcessOrder35Page;
import com.leroy.magmobile.ui.pages.sales.orders.order.ConfirmedOrderPage;
import com.leroy.magmobile.ui.pages.sales.orders.order.OrderActionWithProductCardModel;
import com.leroy.magmobile.ui.pages.sales.orders.order.ProcessOrder35Page;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.utils.ParserUtil;
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
    CartProcessOrder35Page cartProcessOrder35Page;
    ProcessOrder35Page processOrder35Page;
    AddProduct35Page<CartProcessOrder35Page> addProduct35Page;
    EditProduct35Page<CartProcessOrder35Page> editProduct35Page;
    SearchProductPage searchProductPage;
    OrderActionWithProductCardModel actionWithProductCardModal;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeGroups(groups = NEED_PRODUCTS_GROUP)
    private void findProducts() {
        lmCodes = apiClientProvider.getProductLmCodes(2, false, false);
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
        salesDocumentData = startFromScreenWithOrderDraft(true);
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
            salesDocumentData = startFromScreenWithOrderDraft(true);
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
        SalesDocumentData salesDocumentData = startFromScreenWithOrderDraft(true);

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
        SalesDocumentData salesDocumentData = startFromScreenWithOrderDraft(null,
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
        SalesDocumentData salesDocumentData = startFromScreenWithOrderDraftWithDiscount();
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
        salesDocumentData = startFromScreenWithOrderDraft(
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

        salesDocumentData = startFromScreenWithOrderDraft(null,
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
        salesDocumentData = startFromScreenWithOrderDraft(true);

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
        searchProductPage = cartProcessOrder35Page.clickAddProductButton()
                .verifyRequiredElements();
    }

    /**
     * Нажмите на мини-карточку товара в списке доступных товаров
     */
    private void stepClickProductCard(int index) throws Exception {
        salesDocumentData.setProductDataInEditModeNow(
                salesDocumentData.getOrderAppDataList().get(0).getProductCardDataList().get(index - 1));
        actionWithProductCardModal = cartProcessOrder35Page.clickCardByIndex(index)
                .verifyRequiredElements();
    }

    /**
     * Выберите параметр Изменить количество в модальном окне
     */
    private void stepClickChangeQuantityInModalWindow() {
        editProduct35Page = actionWithProductCardModal.clickChangeQuantityMenuItem()
                .verifyRequiredElements();
    }

    /**
     * Измените количество товара на странице для редактирования товара
     */
    private void stepChangeQuantityProduct(int quantity) {
        OrderAppData curOrder = salesDocumentData.getOrderDataInEditModeNow();
        curOrder.changeProductQuantity(salesDocumentData.getProductDataInEditModeNow(), quantity);
        curOrder.setTotalWeight(null);
        editProduct35Page.enterQuantityOfProduct(quantity, true);
    }

    /**
     * Нажмите на кнопку Сохранить
     */
    private void stepSaveEditProductChanges(boolean verifyChanges) throws Exception {
        cartProcessOrder35Page = editProduct35Page.clickSaveButton();
        cartProcessOrder35Page.waitUntilTotalOrderPriceIs(salesDocumentData.getOrderAppDataList().get(0).getTotalPrice());
        if (verifyChanges)
            cartProcessOrder35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

    /**
     * Введите ЛМ код товара на экране поиска товаров
     */
    private void stepSearchForProduct(String searchText) {
        searchProductPage.enterTextInSearchFieldAndSubmit(searchText);
        addProduct35Page = new AddProduct35Page<>(CartProcessOrder35Page.class)
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_ORDER);
    }

    /**
     * Изменить кол-во товара и Добавить товар в заказ
     */
    private void stepAddProductInOrderWithEditQuantity(Double quantity, boolean verifyProducts) {
        ProductOrderCardAppData productData = addProduct35Page.getProductOrderDataFromPage();
        productData.setAvailableTodayQuantity(null);

        if (quantity != null) {
            productData.setSelectedQuantity(quantity);
            productData.setTotalPrice(ParserUtil.multiply(quantity, productData.getPrice(), 2));
            addProduct35Page.enterQuantityOfProduct((int) Math.round(quantity), false);
        }

        salesDocumentData.getOrderAppDataList().get(0).addFirstProduct(productData);
        salesDocumentData.getOrderAppDataList().get(0).setTotalWeight(null);

        cartProcessOrder35Page = addProduct35Page.clickAddIntoOrderButton();
        if (verifyProducts)
            cartProcessOrder35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

    /**
     * Добавить товар в заказ
     */
    private void stepAddProductInOrder(boolean verifyProducts) {
        stepAddProductInOrderWithEditQuantity(null, verifyProducts);
    }


}
