package com.leroy.magportal.ui.tests;

import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.CustomerClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.customer.CustomerResponseBodyData;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.constants.TestDataConstants;
import com.leroy.magportal.ui.models.customers.CustomerWebData;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.models.salesdoc.*;
import com.leroy.magportal.ui.pages.cart_estimate.CartEstimatePage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.cart_estimate.modal.SubmittedEstimateModal;
import com.leroy.magportal.ui.pages.customers.CreateCustomerForm;
import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EstimateTest extends WebBaseSteps {

    // Test groups
    private final static String NEED_ACCESS_TOKEN_GROUP = "need_access_token";
    private final static String NEED_PRODUCTS_GROUP = "need_products";

    private List<ProductItemData> productList;

    private String customerPhone = "1111111111";

    @BeforeGroups(groups = NEED_PRODUCTS_GROUP)
    private void findProducts() {
        productList = apiClientProvider.getProducts(3);
    }

    @BeforeGroups(NEED_ACCESS_TOKEN_GROUP)
    private void addAccessTokenToSessionData() {
        sessionData.setAccessToken(getAccessToken());
    }

    @Step("Создаем клиента через API")
    private SimpleCustomerData createCustomerByApi() {
        CustomerClient customerClient = apiClientProvider.getCustomerClient();
        CustomerData customerData = new CustomerData();
        customerData.generateRandomValidRequiredData(true, true);
        Response<CustomerResponseBodyData> resp = customerClient.createCustomer(customerData);
        customerData = customerClient.assertThatIsCreatedAndGetData(resp, customerData);

        SimpleCustomerData uiCustomerData = new SimpleCustomerData();
        uiCustomerData.setName(customerData.getFirstName(), customerData.getLastName());
        uiCustomerData.setPhoneNumber(customerData.getMainPhoneFromCommunication());
        uiCustomerData.setEmail(customerData.getMainEmailFromCommunication());

        return uiCustomerData;
    }

    @Test(description = "C3302188 Create estimate", groups = NEED_PRODUCTS_GROUP)
    public void testCreateEstimate() throws Exception {
        String testProductLmCode = productList.get(0).getLmCode();
        EstimatePage estimatePage = loginAndGoTo(EstimatePage.class);

        // Step 1
        step("Нажмите на активную кнопку '+Создать корзину' в правом верхнем углу экрана");
        estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements(EstimatePage.PageState.CREATING_EMPTY);

        // Step 2
        step("Введите ЛМ товара в поле 'Добавление товара' и нажмите Enter");
        estimatePage.enterTextInSearchProductField(testProductLmCode);
        estimatePage.shouldEstimateHasProducts(Collections.singletonList(testProductLmCode));
        SalesDocWebData estimateData = estimatePage.getSalesDocData();
        anAssert.isFalse(estimateData.getNumber().isEmpty(),
                "Номер сметы отсутствует");
        anAssert.isEquals(estimateData.getAuthorName(), EnvConstants.BASIC_USER_FIRST_NAME,
                "Ожидалось другое имя создателя сметы");

        // Step 3
        step("Нажмите на кнопку 'Добавить клиента'");
        estimatePage.clickAddCustomer()
                .shouldAddingNewUserAvailable();

        // Step 4
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        estimatePage.selectCustomerByPhone(customerPhone)
                .shouldSelectedCustomerHasPhone(customerPhone);
        estimateData.setClient(estimatePage.getCustomerData());

        // Step 5
        step("Нажмите на кнопку 'Создать'");
        SubmittedEstimateModal submittedEstimateModal = estimatePage.clickCreateButton();
        submittedEstimateModal.verifyRequiredElements()
                .shouldEstimateNumberIs(estimateData.getNumber())
                .shouldPricesAreFixedAt("5");

        // Step 6
        step("Кликнете правой кнопкой мышки по экрану");
        estimatePage = submittedEstimateModal.closeWindow();
        estimateData.setStatus(SalesDocumentsConst.States.CONFIRMED.getUiVal());
        estimatePage.shouldEstimateHasData(estimateData)
                .shouldDocumentIsPresent(estimateData.getNumber());
    }

    @Test(description = "C3302208 Search product by lm code", groups = NEED_PRODUCTS_GROUP)
    public void testSearchProductByLmCodeInEstimate() throws Exception {
        ProductItemData testProduct = productList.get(0);
        EstimatePage estimatePage = loginAndGoTo(EstimatePage.class);

        // Step #1
        step("Нажмите на активную кнопку '+Создать смету' в правом верхнем углу экрана");
        estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements(EstimatePage.PageState.CREATING_EMPTY);

        // Step #2
        step("Введите ЛМ товара (например 11590966) в поле 'Добавление товара' и нажмите Enter");
        estimatePage.enterTextInSearchProductField(testProduct.getLmCode());

        SalesDocWebData expectedEstimateData = new SalesDocWebData();
        expectedEstimateData.setAuthorName(EnvConstants.BASIC_USER_FIRST_NAME);
        expectedEstimateData.setStatus(SalesDocumentsConst.States.DRAFT.getUiVal());

        ProductOrderCardWebData expectedProduct = new ProductOrderCardWebData();
        expectedProduct.setTitle(testProduct.getTitle());
        expectedProduct.setBarCode(testProduct.getBarCode());
        expectedProduct.setLmCode(testProduct.getLmCode());
        expectedProduct.setPrice(testProduct.getPrice());
        expectedProduct.setSelectedQuantity(1.0);
        expectedProduct.setTotalPrice(testProduct.getPrice());

        OrderWebData expOrderData = new OrderWebData();
        expOrderData.setProductCount(1);
        expOrderData.setTotalPrice(testProduct.getPrice());
        expOrderData.setProductCardDataList(Collections.singletonList(expectedProduct));

        expectedEstimateData.setOrders(Collections.singletonList(expOrderData));
        estimatePage.shouldEstimateHasData(expectedEstimateData);
    }

    @Test(description = "C3302209 Search product by barcode", groups = NEED_PRODUCTS_GROUP)
    public void testSearchProductByBarcodeInEstimate() throws Exception {
        ProductItemData testProduct = productList.get(0);
        EstimatePage estimatePage = isStartFromScratch() ? loginAndGoTo(EstimatePage.class) :
                new EstimatePage(context);

        // Step #1
        step("Нажмите на активную кнопку '+Создать смету' в правом верхнем углу экрана");
        estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements(EstimatePage.PageState.CREATING_EMPTY);

        // Step #2
        step("Введите баркод товара (например 5411 510 510 203) в поле 'Добавление товара' и нажмите Enter");
        estimatePage.enterTextInSearchProductField(testProduct.getBarCode());

        SalesDocWebData expectedEstimateData = new SalesDocWebData();
        expectedEstimateData.setAuthorName(EnvConstants.BASIC_USER_FIRST_NAME);
        expectedEstimateData.setStatus(SalesDocumentsConst.States.DRAFT.getUiVal());

        ProductOrderCardWebData expectedProduct = new ProductOrderCardWebData();
        expectedProduct.setTitle(testProduct.getTitle());
        expectedProduct.setBarCode(testProduct.getBarCode());
        expectedProduct.setLmCode(testProduct.getLmCode());
        expectedProduct.setPrice(testProduct.getPrice());
        expectedProduct.setSelectedQuantity(1.0);
        expectedProduct.setTotalPrice(testProduct.getPrice());

        OrderWebData expOrderData = new OrderWebData();
        expOrderData.setProductCount(1);
        expOrderData.setTotalPrice(testProduct.getPrice());
        expOrderData.setProductCardDataList(Collections.singletonList(expectedProduct));

        expectedEstimateData.setOrders(Collections.singletonList(expOrderData));
        estimatePage.shouldEstimateHasData(expectedEstimateData);
    }

    @Test(description = "C3302211 Add new product to estimate", groups = NEED_PRODUCTS_GROUP)
    public void testAddNewProductToEstimate() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        ProductItemData testProduct2 = productList.get(1);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        SalesDocWebData estimateData = estimatePage.getSalesDocData();

        // Step 1
        step("Нажмите на поле 'Добавление товара'");
        estimatePage.enterTextInSearchProductField(testProduct2.getLmCode());

        ProductOrderCardWebData newProduct = new ProductOrderCardWebData();
        newProduct.setTitle(testProduct2.getTitle());
        newProduct.setBarCode(testProduct2.getBarCode());
        newProduct.setLmCode(testProduct2.getLmCode());
        newProduct.setPrice(testProduct2.getPrice());
        newProduct.setSelectedQuantity(1.0);
        newProduct.setTotalPrice(testProduct2.getPrice());

        OrderWebData order = estimateData.getOrders().get(0);
        order.setProductCardDataList(Arrays.asList(newProduct,
                order.getProductCardDataList().get(0)));
        order.setProductCount(order.getProductCount() + 1);
        order.setTotalWeight(null);
        order.setTotalPrice(order.getTotalPrice() + newProduct.getTotalPrice());
        estimatePage.shouldEstimateHasData(estimateData);
    }

    @Test(description = "C3302212 Copy existing product to estimate", groups = NEED_PRODUCTS_GROUP)
    public void testCopyExistingProductToEstimate() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        ProductItemData testProduct2 = productList.get(1);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
            estimatePage.enterTextInSearchProductField(testProduct2.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        SalesDocWebData estimateData = estimatePage.getSalesDocData();
        OrderWebData orderWebData = estimateData.getOrders().get(0);
        ProductOrderCardWebData copyProduct = orderWebData.getProductCardDataList().get(0);
        orderWebData.addFirstProduct(copyProduct, true);

        // Step 1
        step("Нажмите на кнопку 'Добавить еще раз' любого товара в списке товаров сметы");
        estimatePage.copyProductByIndex(1);
        estimatePage.shouldEstimateHasData(estimateData);
    }

    @Test(description = "C3302213 Change quantity of product", groups = NEED_PRODUCTS_GROUP)
    public void testChangeQuantityOfProductInEstimate() throws Exception {
        // Pre-condition
        int newQuantity = 5;
        ProductItemData testProduct1 = productList.get(0);
        ProductItemData testProduct2 = productList.get(1);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
            estimatePage.enterTextInSearchProductField(testProduct2.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        SalesDocWebData estimateData = estimatePage.getSalesDocData();
        estimateData.getOrders().get(0).changeProductQuantity(0, newQuantity, true);

        // Step 1
        step("Нажмите на поле изменения количества товара и введите новое значение");
        estimatePage.changeQuantityProductByIndex(newQuantity, 1);
        estimatePage.shouldEstimateHasData(estimateData);

        // Step 2
        step("Нажмите на плашку '+' или '-'");
        boolean clickPlus = new Random().nextBoolean();
        if (clickPlus) {
            estimatePage.increaseQuantityProductByIndex(1);
            estimateData.getOrders().get(0).changeProductQuantity(
                    0, newQuantity + 1, true);
        } else {
            estimatePage.decreaseQuantityProductByIndex(1);
            estimateData.getOrders().get(0).changeProductQuantity(
                    0, newQuantity - 1, true);
        }
        estimatePage.shouldEstimateHasData(estimateData);

        // Step 3
        step("Обновите страницу");
        // Если сразу после изменения кол-ва обновить страницу, то данные могут не сохраниться
        Thread.sleep(3000); // Так делать плохо!
        estimatePage.reloadPage();
        new EstimatePage(context).shouldEstimateHasData(estimateData);
    }

    @Test(description = "C3302216 Ordered quantity of product more than existing", groups = NEED_PRODUCTS_GROUP)
    public void testOrderedQuantityOfProductMoreThanExisting() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        Number newQuantity = testProduct1.getAvailableStock() + 1;
        SalesDocWebData estimateData = estimatePage.getSalesDocData();
        estimateData.getOrders().get(0).changeProductQuantity(0, newQuantity, true);

        // Step 4
        step("Измените количество товара на число, большее доступного для заказа");
        estimatePage.changeQuantityProductByIndex(newQuantity, 1);
        estimatePage.shouldEstimateHasData(estimateData);
        estimatePage.shouldProductAvailableStockLabelIsRed(1, 1);
    }

    @Test(description = "C3302214 Remove product from estimate", groups = NEED_PRODUCTS_GROUP)
    public void testRemoveProductFromEstimate() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        ProductItemData testProduct2 = productList.get(1);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
            estimatePage.enterTextInSearchProductField(testProduct2.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        SalesDocWebData estimateData = estimatePage.getSalesDocData();

        // Step 1
        step("Нажмите на кнопку 'Удалить' в мини-карточке выбранного товара и подтвердите удаление");
        estimatePage.removeProductByIndex(1);
        estimateData.getOrders().get(0).removeProduct(0, true);
        estimatePage.shouldEstimateHasData(estimateData);
    }

    @Test(description = "C3302215 Remove last product from estimate", groups = NEED_PRODUCTS_GROUP)
    public void testRemoveLastProductFromEstimate() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        String docNumber = estimatePage.getDocumentNumber();

        // "Удаляем 'лишние' продукты из сметы, если они есть"
        int productCount = estimatePage.getProductDataList().size();
        for (int i = 0; i < productCount - 1; i++) {
            estimatePage.removeProductByIndex(1);
        }

        // Step 1
        step("Нажмите на кнопку 'Удалить' в мини-карточке выбранного товара и подтвердите удаление");
        estimatePage.removeProductByIndex(1)
                .verifyRequiredElements(EstimatePage.PageState.EMPTY)
                .shouldDocumentIsNotPresent(docNumber);
    }

    // Client for Estimate

    @Test(description = "C3302189 Search client by phone number")
    public void testSearchClientByPhoneNumberInEstimate() throws Exception {
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;

        EstimatePage estimatePage;
        if (isStartFromScratch())
            estimatePage = loginAndGoTo(EstimatePage.class)
                    .clickCreateEstimateButton();
        else {
            estimatePage = new EstimatePage(context);
            estimatePage.reloadPage();
        }

        // Step 1
        step("Нажмите на кнопку 'Добавить клиента'");
        estimatePage.clickAddCustomer()
                .shouldAddingNewUserAvailable();

        // Step 2 and 3
        step("Введите номер телефона, нажмите, 'Enter', кликните по мини-карточке нужного клиента");
        estimatePage.selectCustomerByPhone(customerData.getPhoneNumber())
                .shouldSelectedCustomerIs(customerData);
    }

    @Test(description = "C3302190 Search client by email")
    public void testSearchClientByEmailInEstimate() throws Exception {
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;

        EstimatePage estimatePage;
        if (isStartFromScratch())
            estimatePage = loginAndGoTo(EstimatePage.class)
                    .clickCreateEstimateButton();
        else {
            estimatePage = new EstimatePage(context);
            estimatePage.reloadPage();
        }

        // Step 1
        step("Нажмите на кнопку 'Добавить клиента'");
        estimatePage.clickAddCustomer()
                .shouldAddingNewUserAvailable();

        // Step 2
        step("Нажмите на поле Телефон");
        estimatePage.shouldSearchTypeOptionsAreCorrected();

        // Step 3
        step("Выберите параметр Email и введите нужный почтовый адрес (rr@r.ru), нажмите Enter");
        estimatePage.selectSearchType(CartEstimatePage.SearchType.EMAIL);
        estimatePage.selectCustomerByEmail(customerData.getEmail())
                .shouldSelectedCustomerIs(customerData);
    }

    @Test(description = "C3302191 Search client by service card")
    public void testSearchClientByServiceCardInEstimate() throws Exception {
        // Test data
        SimpleCustomerData customerData = TestDataConstants.CUSTOMER_WITH_SERVICE_CARD;

        EstimatePage estimatePage;
        if (isStartFromScratch())
            estimatePage = loginAndGoTo(EstimatePage.class)
                    .clickCreateEstimateButton();
        else {
            estimatePage = new EstimatePage(context);
            estimatePage.reloadPage();
        }

        // Step 1
        step("Нажмите на кнопку 'Добавить клиента'");
        estimatePage.clickAddCustomer()
                .shouldAddingNewUserAvailable();

        // Step 2
        step("Нажмите на поле Телефон");
        estimatePage.shouldSearchTypeOptionsAreCorrected();

        // Step 3
        step("Выберите параметр Карта клиента");
        estimatePage.selectSearchType(CartEstimatePage.SearchType.CARD);
        estimatePage.clickCardSearchFieldAndCheckThatDefaultValueIs(customerData.getFirstPartCardNumber());

        // Step 4
        step("Введите нужный номер сервисной карты (1 2083 5271 8), нажмите Enter");
        estimatePage.enterCardNumberInSearchCustomerField(customerData.getSecondPartCardNumber());
        estimatePage.shouldSelectedCustomerHasCardNumber(customerData.getCardNumber());
    }

    @Test(description = "C3302195 Change client to another one")
    public void testChangeClientToAnotherOne() throws Exception {
        SimpleCustomerData customer1 = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        SimpleCustomerData customer2 = TestDataConstants.SIMPLE_CUSTOMER_DATA_2;
        step("Выполнение предусловий: авторизуемся, заходим на страницу сметы, выбираем пользователя");
        EstimatePage estimatePage;
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class)
                    .clickCreateEstimateButton();
        } else {
            estimatePage = new EstimatePage(context);
            estimatePage.reloadPage();
        }

        estimatePage.clickAddCustomer()
                .selectCustomerByPhone(customer1.getPhoneNumber());

        // Step 1 and 2
        step("Нажмите на '...' справа в карточке клиента и Выберите параметр 'Выбрать другого клиента'");
        estimatePage.clickOptionSelectAnotherCustomer()
                .shouldAddingNewUserAvailable();

        // Step 3 and 4
        step("Введите номер телефона другого клиента и нажмите Enter");
        estimatePage.selectCustomerByPhone(customer2.getPhoneNumber())
                .shouldSelectedCustomerIs(customer2);
    }

    @Test(description = "C3302194 Add new client to estimate")
    public void testAddNewClientToEstimate() throws Exception {
        String unusedPhoneNumber = apiClientProvider.findUnusedPhoneNumber();

        step("Выполнение предусловий: авторизуемся, заходим на страницу сметы");
        EstimatePage estimatePage = loginAndGoTo(EstimatePage.class)
                .clickCreateEstimateButton();

        // Step 1
        step("Нажмите на кнопку 'Добавить клиента'");
        estimatePage.clickAddCustomer()
                .shouldAddingNewUserAvailable();

        // Step 2
        step("Введите неиспользуемый ранее номер телефона и нажмите Enter");
        estimatePage.enterPhoneInSearchCustomerField(unusedPhoneNumber);

        // Step 3
        step("Нажмите на кнопку 'Создать клиента'");
        CreateCustomerForm createCustomerForm = estimatePage.clickCreateCustomerButton();
        createCustomerForm.verifyRequiredElements();

        // Step 4, 5, 6
        step("Заполните поле Имя. Выберите Пол и тип Телефона");
        CustomerWebData customerWebData = new CustomerWebData();
        customerWebData.setRandomRequiredData();
        customerWebData.setPhoneNumber(null);
        customerWebData.setLastName(customerWebData.getRandomCyrillicCharacters(5));
        createCustomerForm.enterCustomerData(customerWebData);

        // Step 7
        step("Нажмите на Показать все поля");
        createCustomerForm.clickShowAllFieldsButton()
                .verifyAllAdditionalFields();

        // Step 8
        step("Нажмите на Скрыть дополнительные поля");
        createCustomerForm.clickHideAllFieldsButton()
                .verifyRequiredElements();

        // Step 9
        step("Нажмите на Создать");
        createCustomerForm.clickCreateButton();
        estimatePage.shouldSelectedCustomerIs(new SimpleCustomerData().toBuilder()
                .name(StringUtils.capitalize(customerWebData.getFirstName()) + " " +
                        StringUtils.capitalize(customerWebData.getLastName()))
                .phoneNumber(unusedPhoneNumber)
                .build());
    }

    @Test(description = "C3302197 Change parameters of client", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testChangeParametersOfClient() throws Exception {
        SimpleCustomerData customerData = createCustomerByApi();
        step("Выполнение предусловий: авторизуемся, заходим на страницу сметы, выбираем пользователя");
        EstimatePage estimatePage;
        if (!isStartFromScratch()) {
            estimatePage = new EstimatePage(context);
            estimatePage.reloadPage();
        } else {
            estimatePage = loginAndGoTo(EstimatePage.class)
                    .clickCreateEstimateButton();
        }
        estimatePage.clickAddCustomer()
                .selectCustomerByPhone(customerData.getPhoneNumber());

        // Step 1, 2
        step("Нажмите на '...' справа в карточке клиента и Выберите параметр Редактировать данные клиента");
        CreateCustomerForm createCustomerForm = estimatePage.clickOptionEditCustomer();

        // Step 3
        step("Нажмите на Добавить еще телефон");
        createCustomerForm.clickAddPhoneButton();

        // Step 4
        step("Введите телефон, выберите параметр Рабочий и проставьте чекбокс Основной.");
        String secondPhone = "+7" + RandomStringUtils.randomNumeric(10);
        createCustomerForm.enterTextInPhoneInputField(2, secondPhone)
                .clickTypePhone(2, CreateCustomerForm.CommunicationType.WORK)
                .makePhoneAsMain(2);

        // Step 5
        step("Нажмите на кнопку 'Сохранить'");
        createCustomerForm.clickCreateButton();
        customerData.setPhoneNumber(secondPhone);
        estimatePage.shouldSelectedCustomerIs(customerData);
    }

    @Test(description = "C3302196 Validation: Estimate without client", groups = NEED_PRODUCTS_GROUP)
    public void testValidateCreationEstimateWithoutClient() throws Exception {
        step("Выполнение предусловий: авторизуемся, заходим на страницу сметы");
        String testProductLmCode = productList.get(0).getLmCode();
        EstimatePage estimatePage;
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class)
                    .clickCreateEstimateButton();
        } else {
            estimatePage = new EstimatePage(context);
            estimatePage.removeSelectedCustomer();
        }

        // Step 1
        step("Введите ЛМ товара в поле 'Добавление товара' и нажмите Enter");
        estimatePage.enterTextInSearchProductField(testProductLmCode);
        estimatePage.shouldEstimateHasProducts(Collections.singletonList(testProductLmCode))
                .shouldErrorTooltipCustomerIsRequiredVisible();

        // Step 2
        step("Нажмите на кнопку 'Создать'");
        estimatePage.<EstimatePage>clickCreateButton()
                .shouldErrorTooltipCustomerIsRequiredVisible();
    }

    // Delete Estimate

    private void testRemoveEstimate(String estimateId) throws Exception {
        EstimatePage estimatePage;
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class);
        } else {
            estimatePage = new EstimatePage(context);
        }
        estimatePage.openPageWithEstimate(estimateId);

        step("Нажмите иконку корзины и удалите смету");
        estimatePage.removeEstimate()
                .verifyRequiredElements(EstimatePage.PageState.EMPTY)
                .shouldDocumentIsNotPresent(estimateId);
    }

    @Test(description = "C23389036 Удаление сметы в статусе Черновик", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testRemoveDraftEstimate() throws Exception {
        String estimateId = apiClientProvider.createDraftEstimateAndGetCartId();
        testRemoveEstimate(estimateId);
    }

    @Test(description = "C23398452 Удаление сметы в статусе Создан", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testRemoveConfirmedEstimate() throws Exception {
        String estimateId = apiClientProvider.createConfirmedEstimateAndGetCartId();
        testRemoveEstimate(estimateId);
    }

    // Search

    @Test(description = "C3302186 Search of documents", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testSearchDocumentEstimate() throws Exception {
        step("Выполнение предусловий");
        String estimateId = apiClientProvider.createDraftEstimateAndGetCartId();
        EstimatePage estimatePage = loginAndGoTo(EstimatePage.class);

        // Step 1, 2
        step("Введите номер искомой сметы полностью в поле 'Номер сметы' и нажмите Enter");
        estimatePage.enterTextInSearchDocumentField(estimateId);
        estimatePage.shouldDocumentListNumbersEqual(Collections.singletonList(estimateId));

        // Step 3
        step("Введите номер искомой сметы частично, нажмите Enter");
        String partEstimateId = estimateId.substring(0, 4);
        estimatePage.enterTextInSearchDocumentField(partEstimateId)
                .shouldDocumentListHaveNumberContains(partEstimateId)
                .shouldDocumentListIsNotEmpty();

        // Step 4, 5, 6
        step("Нажмите на кнопку профиля пользователя в правом верхнем углу и выберите другой магазин");
        List<String> docNumberList = estimatePage.getDocumentDataList()
                .stream().map(ShortSalesDocWebData::getNumber).collect(Collectors.toList());
        estimatePage.selectShopInUserProfile("35");
        estimatePage.shouldDocumentListHaveNumberContains(partEstimateId)
                .shouldDocumentListNumbersNotEqual(docNumberList);
    }

}
