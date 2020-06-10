package com.leroy.magmobile.ui.tests.sales;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.ui.models.CustomerData;
import com.leroy.magmobile.ui.models.sales.OrderAppData;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.models.sales.ShortSalesDocumentData;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.customers.EditCustomerContactDetailsPage;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.EditProduct35Page;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import com.leroy.magmobile.ui.pages.sales.orders.estimate.*;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

public class EstimateTest extends SalesBaseTest {

    private String firstCustomerPhone = "1111111111";
    private String secondCustomerPhone = "2222222222";

    private List<String> productLmCodes;

    @BeforeClass
    private void findProducts() {
        productLmCodes = apiClientProvider.getProductLmCodes(2);
    }

    @Step("Pre-condition: Создание сметы")
    private void startFromScreenWithCreatedEstimate(List<String> lmCodes,
                                                    boolean isConfirmed) throws Exception {
        boolean byApi = true; // should be true; (false - if only backend has problems)
        if (isStartFromScratch()) {
            if (byApi) {
                String estimateId = isConfirmed ? apiClientProvider.createConfirmedEstimateAndGetCartId(lmCodes) :
                        apiClientProvider.createDraftEstimateAndGetCartId(lmCodes);
                MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                        MainSalesDocumentsPage.class);
                SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
                salesDocumentsPage.searchForDocumentByTextAndSelectIt(estimateId);
            } else {
                String lmCode = getAnyLmCodeProductWithoutSpecificOptions();
                MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                        MainSalesDocumentsPage.class);
                SaleTypeModalPage modalPage = mainSalesDocumentsPage.clickCreateSalesDocumentButton();
                modalPage.clickEstimateMenuItem()
                        .clickCustomerField()
                        .selectSearchType(SearchCustomerPage.SearchType.BY_PHONE)
                        .enterTextInSearchField(firstCustomerPhone)
                        .selectCustomerFromSearchList(1)
                        .clickProductAndServiceButton()
                        .enterTextInSearchFieldAndSubmit(lmCode);
                AddProduct35Page<EstimatePage> addProduct35Page = new AddProduct35Page<>(EstimatePage.class);
                addProduct35Page.clickAddIntoEstimateButton();
            }
        }
    }

    @Test(description = "C22797068 Создать смету с экрана Документы продажи")
    public void testCreatingEstimateFromSalesDocumentsScreen() throws Exception {
        // Test data
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions();
        // Pre-condition
        MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);

        // Step 1
        step("Нажать кнопку Оформить продажу");
        SaleTypeModalPage modalPage = mainSalesDocumentsPage.clickCreateSalesDocumentButton();
        modalPage.verifyRequiredElementsWhenFromSalesDocuments();

        // Step 2
        step("Выбрать параметр Смета");
        EstimatePage estimatePage = modalPage.clickEstimateMenuItem();
        EstimatePage.PageState pageState = EstimatePage.PageState.builder()
                .customerIsSelected(false).productIsAdded(false).build();
        estimatePage.verifyRequiredElements(pageState);

        // Step 3
        step("Нажмите на поле Клиенты");
        SearchCustomerPage searchCustomerPage = estimatePage.clickCustomerField();
        searchCustomerPage.verifyRequiredElements();

        // Step 4
        step("Введите номер телефона/ №карты клиента/ эл. почту");
        CustomerData customerData = searchCustomerPage.selectSearchType(SearchCustomerPage.SearchType.BY_PHONE)
                .enterTextInSearchField(firstCustomerPhone)
                .getCustomerDataFromSearchListByIndex(1);
        estimatePage = searchCustomerPage.selectCustomerFromSearchList(1);
        pageState = EstimatePage.PageState.builder()
                .customerIsSelected(true).productIsAdded(false).build();
        estimatePage.verifyRequiredElements(pageState);
        estimatePage.shouldSelectedCustomerIs(customerData);

        // Step 5
        step("Нажмите на кнопку +товары и услуги");
        SearchProductPage searchProductPage = estimatePage.clickProductAndServiceButton();
        searchProductPage.verifyRequiredElements();

        // Step 6
        step("Введите ЛМ код товара или название товара или отсканируйте товар");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page<EstimatePage> addProduct35Page = new AddProduct35Page<>(EstimatePage.class);
        addProduct35Page.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_ESTIMATE);

        // Step 7
        step("Нажмите на Добавить в смету");
        estimatePage = addProduct35Page.clickAddIntoEstimateButton();
        pageState = EstimatePage.PageState.builder()
                .customerIsSelected(true).productIsAdded(true).build();
        estimatePage.verifyRequiredElements(pageState);

        // Step 8
        step("Нажмите на Создать");
        Double expectedTotalPrice = estimatePage.getTotalPrice();
        String documentNumber = estimatePage.getDocumentNumber(true);
        EstimateSubmittedPage estimateSubmittedPage = estimatePage.clickCreateButton()
                .verifyRequiredElements();

        // Step 9
        step("Нажать на Перейти в список документов");
        ShortSalesDocumentData expectedSalesDocument = new ShortSalesDocumentData();
        expectedSalesDocument.setDocumentTotalPrice(expectedTotalPrice);
        expectedSalesDocument.setDocumentState(SalesDocumentsConst.States.CONFIRMED.getUiVal());
        expectedSalesDocument.setTitle(SalesDocumentsConst.Types.ESTIMATE.getUiVal());
        expectedSalesDocument.setNumber(documentNumber);
        SalesDocumentsPage salesDocumentsPage = estimateSubmittedPage.clickSubmitButton();
        salesDocumentsPage.verifyRequiredElements()
                .shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);
        // Post actions
        step("(Доп. шаг) Найти и открыть созданную смету");
        salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                expectedSalesDocument.getNumber());
        new EstimatePage();
    }

    @Test(description = "C22797074 Посмотреть подробнее о товаре", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testViewProductDetailsFromEstimateScreen() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 1), false);

        EstimatePage estimatePage = new EstimatePage();
        ProductOrderCardAppData productCardData = estimatePage.getCardDataListFromPage()
                .get(0);

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров сметы");
        EstimateActionWithProductCardModal modalPage = estimatePage.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        step("Выберите параметр Подробнее о товаре");
        ProductDescriptionPage productDescriptionPage = modalPage.clickProductDetailsMenuItem();
        productDescriptionPage.verifyRequiredElements(false)
                .shouldProductLMCodeIs(productCardData.getLmCode());

        // Post-action:
        step("Вернитесь обратно в смету");
        productDescriptionPage.returnBack(EstimatePage.class);
    }

    @Test(description = "C22797073 Изменить количество добавленного товара", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testChangeProductQuantityFromEstimateScreen() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 1), false);

        EstimatePage estimatePage = new EstimatePage();
        ProductOrderCardAppData productCardDataBefore = estimatePage.getCardDataListFromPage()
                .get(0);

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров сметы");
        EstimateActionWithProductCardModal modalPage = estimatePage.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        step("Выберите параметр Изменить количество");
        EditProduct35Page<EstimatePage> editProduct35Page = modalPage.clickChangeQuantityMenuItem();
        editProduct35Page.verifyRequiredElements();

        // Step 3
        step("Измените количество товара");
        String testEditQuantityValue = String.valueOf(new Random().nextInt(10) + 1);
        Double expectedTotalCost = Double.parseDouble(testEditQuantityValue) * productCardDataBefore.getPrice();
        editProduct35Page.enterQuantityOfProduct(testEditQuantityValue, true);

        // Step 4
        step("Нажмите на кнопку Сохранить");
        estimatePage = editProduct35Page.clickSaveButton();
        ProductOrderCardAppData productCardDataAfter = estimatePage.getCardDataListFromPage()
                .get(0);

        softAssert().isEquals(productCardDataAfter.getPrice(),
                productCardDataBefore.getPrice(),
                "Цена товара изменилась");
        softAssert().isEquals(productCardDataAfter.getTotalPrice(), expectedTotalCost,
                "Неверная сумма для выбранного товара");
        softAssert().isEquals(productCardDataAfter.getSelectedQuantity(), Double.parseDouble(testEditQuantityValue),
                "Неверное выбранное кол-во товара");
        softAssert().verifyAll();
    }

    @Test(description = "C22797078 Преобразовать смету в корзину", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testTransformEstimateToBasket() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 1), true);

        EstimatePage estimatePage = new EstimatePage();
        // Collect test data from page
        OrderAppData testEstimateData = estimatePage.getOrderDataFromPage();
        String estimateNumber = estimatePage.getDocumentNumber(true);
        String customerName = estimatePage.getCustomerName();

        // Step 1
        step("Нажмите на кнопку Действия со сметой");
        ActionsWithEstimateModalPage modalPage = estimatePage.clickActionsWithEstimateButton();
        modalPage.verifyRequiredElements();

        // Step 2
        step("Выберите параметр Преобразовать в корзину");
        Cart35Page cart35Page = modalPage.clickTransformToBasketMenuItem();
        cart35Page.verifyRequiredElements(Cart35Page.PageState.builder()
                .productIsAdded(true)
                .build());
        cart35Page.shouldOrderDataIs(testEstimateData);

        // Step 3
        ShortSalesDocumentData expectedEstimateDocument = new ShortSalesDocumentData();
        expectedEstimateDocument.setDocumentTotalPrice(testEstimateData.getTotalPrice());
        expectedEstimateDocument.setDocumentState(SalesDocumentsConst.States.TRANSFORMED.getUiVal());
        expectedEstimateDocument.setTitle(SalesDocumentsConst.Types.ESTIMATE.getUiVal());
        expectedEstimateDocument.setNumber(estimateNumber);
        expectedEstimateDocument.setCustomerName(customerName);

        SalesDocumentsPage salesDocumentsPage = cart35Page.clickBackButton();
        salesDocumentsPage.shouldSalesDocumentIsPresentAndDataMatches(expectedEstimateDocument);

        // Step 4
        salesDocumentsPage.searchForDocumentByTextAndSelectIt(estimateNumber);
        estimatePage = new EstimatePage();
        estimatePage.verifyRequiredElements(EstimatePage.PageState.builder()
                .customerIsSelected(true)
                .productIsAdded(true)
                .transformed(true)
                .build());
    }

    @Test(description = "C22797070 Добавить существующий товар еще раз (из поиска)", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testAddProductIntoEstimateAgain() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 1), false);

        // Step 1
        step("Нажмите на кнопку +товары и услуги");
        EstimatePage estimatePage = new EstimatePage();
        OrderAppData firstEstimateStateData = estimatePage.getOrderDataFromPage();
        ProductOrderCardAppData firstProductInEstimate = firstEstimateStateData.getProductCardDataList().get(0);
        SearchProductPage searchProductPage = estimatePage.clickAddProductButton();
        searchProductPage.verifyRequiredElements();

        // Step 2
        step("Введите ЛМ код товара или название товара или отсканируйте товар, ранее добавленный в смету");
        searchProductPage.searchProductAndSelect(firstProductInEstimate.getLmCode());
        AddProduct35Page<EstimatePage> addProductPage = new AddProduct35Page<>(EstimatePage.class);
        addProductPage.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_ESTIMATE);

        // Step 3
        step("Измените количество товара");
        String quantityForProduct2 = String.valueOf(Math.round(firstProductInEstimate.getSelectedQuantity()) + 1);
        addProductPage.enterQuantityOfProduct(quantityForProduct2, true);

        // Step 4
        step("Нажмите на Добавить в смету");
        OrderAppData estimateData = addProductPage.clickAddIntoEstimateButton().getOrderDataFromPage();
        ProductOrderCardAppData newProduct = estimateData.getProductCardDataList().get(0);
        softAssert().isEquals(estimateData.getProductCount(), 2, "Метка с кол-вом товара не верна");
        softAssert().isEquals(estimateData.getProductCardDataList().size(), 2,
                "Кол-во добавленного в смету товара неверно");
        softAssert().isEquals(newProduct.getSelectedQuantity(),
                Double.parseDouble(quantityForProduct2), "Кол-во товара №2 неверно");
        softAssert().isEquals(estimateData.getTotalPrice(),
                firstProductInEstimate.getTotalPrice() +
                        newProduct.getTotalPrice(), "Сумма итого неверна");
        softAssert().isTrue(firstEstimateStateData.getTotalWeight() < estimateData.getTotalWeight(),
                "Вес должен был увеличиться после добавления нового товара");
        softAssert().verifyAll();
    }

    @Test(description = "C22797071 Удалить товар из сметы", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testRemoveProductFromEstimate() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 2), false);

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров сметы");
        EstimatePage estimatePage = new EstimatePage();
        OrderAppData estimateDataBefore = estimatePage.getOrderDataFromPage();
        EstimateActionWithProductCardModal actionWithProductCardModalPage = estimatePage.clickCardByIndex(1);
        actionWithProductCardModalPage.verifyRequiredElements();

        // Step 2
        step("Выберите параметр Удалить товар");
        actionWithProductCardModalPage.clickRemoveProductMenuItem();
        ConfirmRemovingProductModal modal = new ConfirmRemovingProductModal();
        modal.verifyRequiredElements();

        // Step 3
        step("Нажмите на Удалить");
        modal.clickConfirmButton();
        estimatePage = new EstimatePage();
        OrderAppData estimateDataAfter = estimatePage.getOrderDataFromPage();
        softAssert().isEquals(estimateDataAfter.getProductCount(), 1, "Метка кол-ва товаров неверна");
        softAssert().isEquals(estimateDataAfter.getProductCardDataList().size(), 1,
                "Ошибочное кол-во товаров на странице");
        softAssert().isTrue(estimateDataBefore.getTotalWeight() > estimateDataAfter.getTotalWeight() &&
                        estimateDataAfter.getTotalWeight() > 0,
                "Вес должен был уменьшиться");
        softAssert().isEquals(estimateDataAfter.getTotalPrice(),
                estimateDataAfter.getProductCardDataList().get(0).getTotalPrice(),
                "Общая стоимость не пересчитана");
        softAssert().verifyAll();
    }

    @Test(description = "C22797072 Удалить последний товар из сметы", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testRemoveLastProductFromEstimate() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 1), false);

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров сметы");
        EstimatePage estimatePage = new EstimatePage();
        String estimateId = estimatePage.getDocumentNumber(true);
        EstimateActionWithProductCardModal actionWithProductCardModalPage = estimatePage.clickCardByIndex(1);
        actionWithProductCardModalPage.verifyRequiredElements();

        // Step 2
        step("Выберите параметр Удалить товар");
        actionWithProductCardModalPage.clickRemoveProductMenuItem();
        ConfirmRemoveLastProductEstimateModal modal = new ConfirmRemoveLastProductEstimateModal();
        modal.verifyRequiredElements();

        // Step 3
        step("Нажмите на Удалить");
        modal.clickConfirmButton();
        SalesDocumentsPage salesDocumentsPage = new SalesDocumentsPage();
        salesDocumentsPage.shouldSalesDocumentIsNotPresent(estimateId);
    }

    @Test(description = "C22797075 Выбрать другого клиента для сметы", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testEditCustomerInEstimate() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 1), false);

        // Step 1
        step("Нажать на кнопку редактирования клиента");
        EstimatePage estimatePage = new EstimatePage();
        String customerNameBefore = estimatePage.getCustomerName();
        EditCustomerModalPage modalPage = estimatePage.clickEditCustomerField();
        modalPage.verifyRequiredElements();

        // Step 2
        step("Выберете параметр Выбрать другого клиента");
        SearchCustomerPage searchCustomerPage = modalPage.clickSelectAnotherCustomer()
                .verifyRequiredElements();

        // Step 3
        step("Введите номер телефона/ №карты клиента/ эл. почту");
        CustomerData customerData = searchCustomerPage.selectSearchType(SearchCustomerPage.SearchType.BY_PHONE)
                .enterTextInSearchField(secondCustomerPhone)
                .getCustomerDataFromSearchListByIndex(1);
        anAssert().isNotEquals(customerData.getName(), customerNameBefore,
                "В поиске мы нашли клиента с тем же именем, который был и до этого");

        // Step 4
        step("Нажмите на мини-карточку нужного клиента");
        estimatePage = searchCustomerPage.selectCustomerFromSearchList(1);
        anAssert().isEquals(estimatePage.getCustomerName(), customerData.getName(),
                "На странице отображается не тот клиент, которого выбрали");
    }

    @Test(description = "C22797076 Изменить контактные данные клиента", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testChangeCustomerContactDetailsInEstimate() throws Exception {
        step("Pre-condition: Создаем смету");
        com.leroy.magmobile.api.data.customer.CustomerData customerData =
                new com.leroy.magmobile.api.data.customer.CustomerData();
        customerData.generateRandomValidRequiredData(true);
        String estimateId = apiClientProvider.createDraftEstimateAndGetCartId(customerData, 1);
        MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);
        SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
        salesDocumentsPage.searchForDocumentByTextAndSelectIt(estimateId);

        // Step 1
        step("Нажать на кнопку редактирования клиента");
        EstimatePage estimatePage = new EstimatePage();
        EditCustomerModalPage modalPage = estimatePage.clickEditCustomerField();
        modalPage.verifyRequiredElements();

        // Step 2
        step("Выберете параметр Изменить контактные данные");
        EditCustomerContactDetailsPage editCustomerContactDetailsPage = modalPage.clickChangeContactDetails()
                .verifyRequiredElements();

        // Step 3
        step("Нажмите на пустое поле Телефон и Введите новый номер");
        String newPhone = RandomStringUtils.randomNumeric(10);
        editCustomerContactDetailsPage.enterNewPhoneNumber(newPhone);
        editCustomerContactDetailsPage.shouldNewPhoneEqualTo(newPhone);

        // Step 4
        step("Нажмите на Сохранить");
        estimatePage = editCustomerContactDetailsPage.clickSaveButton();
        CustomerData expectedCustomer = new CustomerData();
        expectedCustomer.setName(customerData.getFirstName() + " " + customerData.getLastName());
        expectedCustomer.setPhone(newPhone);
        estimatePage.shouldSelectedCustomerIs(expectedCustomer);
    }

    @Test(description = "C22797082 Добавление товара в смету в статусе Создан", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testAddProductInConfirmedEstimate() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 1), true);

        // Step 1
        step("Нажмите на ручку в верхней правой части экрана");
        EstimatePage estimatePage = new EstimatePage();
        OrderAppData estimateDataBefore = estimatePage.getOrderDataFromPage();
        estimatePage.clickEditEstimateButton()
                .shouldEditModeOn();

        // Step 2
        step("Нажмите на кнопку +Товар");
        SearchProductPage searchProductPage = estimatePage.clickAddProductButton();
        searchProductPage.verifyRequiredElements();

        // Step 3
        step("Введите ЛМ код товара или название товара или отсканируйте товар");
        searchProductPage.searchProductAndSelect(productLmCodes.get(1));
        AddProduct35Page<EstimatePage> addProductPage = new AddProduct35Page<>(EstimatePage.class);
        addProductPage.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_ESTIMATE);
        Double priceNewProduct = addProductPage.getPrice();

        // Step 4
        step("Нажмите на Добавить в смету");
        OrderAppData estimateDataAfter = addProductPage.clickAddIntoEstimateButton()
                .getOrderDataFromPage();
        ProductOrderCardAppData newProduct = estimateDataAfter.getProductCardDataList().get(0);
        softAssert().isEquals(estimateDataAfter.getProductCount(), 2, "Метка с кол-вом товара не верна");
        softAssert().isEquals(estimateDataAfter.getProductCardDataList().size(), 2,
                "Кол-во добавленного в смету товара неверно");
        softAssert().isEquals(newProduct.getSelectedQuantity(),
                1.0, "Кол-во товара №2 неверно");
        softAssert().isEquals(estimateDataAfter.getTotalPrice(),
                estimateDataBefore.getTotalPrice() +
                        priceNewProduct, "Сумма итого неверна");
        softAssert().isTrue(estimateDataBefore.getTotalWeight() < estimateDataAfter.getTotalWeight(),
                "Вес должен был увеличиться после добавления нового товара");
        softAssert().verifyAll();

        // Post action
        step("Нажмите 'Сохранить'");
        estimatePage.clickSaveButton();
    }

    @Test(description = "C22797077 Отправить смету на почту (с экрана успеха или из сметы в статусе Создан)",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testSendEstimateByEmail() throws Exception {
        String estimateDraftId = apiClientProvider.createDraftEstimateAndGetCartId(productLmCodes.subList(0, 1));
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 1), true);

        // Step 1
        step("Нажмите на кнопку Действие со сметой");
        EstimatePage estimatePage = new EstimatePage();
        ActionsWithEstimateModalPage actionsWithEstimateModalPage = estimatePage
                .clickActionsWithEstimateButton()
                .verifyRequiredElements();

        // Step 2
        step("Нажмите на Отправить на email");
        SendEmailPage sendEmailPage = actionsWithEstimateModalPage.clickSendEmailMenuItem()
                .verifyRequiredElements();

        // Step 3
        step("Нажмите на Отправить");
        String email = RandomStringUtils.randomAlphabetic(5) + "@mail.com";
        SubmittedSendEmailPage submittedSendEmailPage = sendEmailPage.enterTextInEmailField(email)
                .clickSubmitButton();
        submittedSendEmailPage.shouldSendToThisEmail(email);

        // Step 4
        step("Нажмите на Перейти в список документов");
        SalesDocumentsPage salesDocumentsPage = submittedSendEmailPage.clickSubmitButton();
        salesDocumentsPage.verifyRequiredElements();

        // Step 5
        step("Нажмите на мини-карточку сметы в статусе черновик");
        salesDocumentsPage.searchForDocumentByTextAndSelectIt(estimateDraftId);
        estimatePage = new EstimatePage();

        // Step 6
        step("Нажмите на кнопку Создать");
        EstimateSubmittedPage estimateSubmittedPage = estimatePage.clickCreateButton()
                .verifyRequiredElements();

        // Step 7
        step("Нажмите на Отправить на email");
        sendEmailPage = estimateSubmittedPage.clickSendToEmailButton()
                .verifyRequiredElements();

        // Step 8
        step("Нажмите на Отправить");
        String email2 = RandomStringUtils.randomAlphabetic(9) + "@mail.com";
        submittedSendEmailPage = sendEmailPage.enterTextInEmailField(email2)
                .clickSubmitButton();
        submittedSendEmailPage.shouldSendToThisEmail(email2);

        // Step 9
        step("Нажмите на Перейти в список документов");
        submittedSendEmailPage.clickSubmitButton()
                .verifyRequiredElements();
    }

    @Test(description = "C22797084 Изменение товара в смету в статусе Создан",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testChangeProductInConfirmedEstimate() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 1), true);

        // Step 1
        step("Нажмите на ручку в верхней правой части экрана");
        EstimatePage estimatePage = new EstimatePage();
        OrderAppData estimateDataBefore = estimatePage.getOrderDataFromPage();
        estimatePage.clickEditEstimateButton()
                .shouldEditModeOn();

        // Step 2
        step("Нажмите на ручку редактирования товара");
        EstimateActionWithProductCardModal actionWithProductCardModalPage = estimatePage.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 3
        step("Выберете параметр Изменить количество");
        EditProduct35Page<EstimatePage> editProduct35Page = actionWithProductCardModalPage.clickChangeQuantityMenuItem();
        editProduct35Page.verifyRequiredElements();

        // Step 4
        step("Измените количество товара");
        String newQuantity = String.valueOf(new Random().nextInt(6) + 2);
        editProduct35Page.enterQuantityOfProduct(newQuantity, true);

        // Step 5
        step("Нажмите на Сохранить");
        estimatePage = editProduct35Page.clickSaveButton();

        ProductOrderCardAppData product = estimateDataBefore.getProductCardDataList().get(0);
        product.setSelectedQuantity(ParserUtil.strToDouble(newQuantity));
        product.setTotalPrice(product.getPrice() * ParserUtil.strToDouble(newQuantity));
        estimateDataBefore.setTotalWeight(estimateDataBefore.getTotalWeight() * ParserUtil.strToDouble(newQuantity));
        estimateDataBefore.setTotalPrice(product.getTotalPrice());
        estimatePage.shouldOrderDataIs(estimateDataBefore);

        // Post action
        step("Нажмите 'Сохранить' на странице сметы");
        estimatePage.clickSaveButton();
    }

    @Test(description = "C22797085 Изменение контактных данных клиента в смете в статусе Создан",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testChangeCustomerContactsInConfirmedEstimate() throws Exception {
        step("Pre-condition: Создаем смету в статусе создан");
        com.leroy.magmobile.api.data.customer.CustomerData customerData =
                new com.leroy.magmobile.api.data.customer.CustomerData();
        customerData.generateRandomValidRequiredData(true);
        String estimateId = apiClientProvider.createConfirmedEstimateAndGetCartId(customerData,
                productLmCodes.subList(0, 1));
        MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);
        SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
        salesDocumentsPage.searchForDocumentByTextAndSelectIt(estimateId);

        // Step 1
        step("Нажмите на кнопку редактирования сметы в правом верхнем углу");
        EstimatePage estimatePage = new EstimatePage();
        String customerNameBefore = estimatePage.getCustomerName();
        estimatePage.clickEditEstimateButton()
                .shouldEditModeOn();

        // Step 2
        step("Нажмите на поле Смета для клиента");
        EditCustomerModalPage editCustomerModalPage = estimatePage.clickEditCustomerField()
                .verifyRequiredElements();

        // Step 3
        step("Выберете параметр Изменить контактные данные");
        EditCustomerContactDetailsPage editCustomerContactDetailsPage =
                editCustomerModalPage.clickChangeContactDetails()
                        .verifyRequiredElements();

        // Step 4, 5
        step("Ведите новый номер в пустое поле Телефон");
        String newPhone = RandomStringUtils.randomNumeric(10);
        editCustomerContactDetailsPage.enterNewPhoneNumber(newPhone);
        editCustomerContactDetailsPage.shouldNewPhoneEqualTo(newPhone);

        // Step 6
        step("Нажмите на Сохранить");
        estimatePage = editCustomerContactDetailsPage.clickSaveButton()
                .verifyRequiredElements(EstimatePage.PageState.builder()
                        .customerIsSelected(true)
                        .productIsAdded(true)
                        .editModeOn(true).build());

        CustomerData expectedCustomer = new CustomerData();
        expectedCustomer.setName(customerNameBefore);
        expectedCustomer.setPhone(newPhone);
        estimatePage.shouldSelectedCustomerIs(expectedCustomer);

        // Step 7
        step("Нажмите на кнопку Сохранить");
        estimatePage.clickSaveButton()
                .verifyRequiredElements(EstimatePage.PageState.builder()
                        .customerIsSelected(true)
                        .productIsAdded(true)
                        .confirmed(true).build());
        estimatePage.shouldSelectedCustomerIs(expectedCustomer);
    }

    @Test(description = "C22797086 Удаление товара из сметы в статусе Создан",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testRemoveProductFromConfirmedEstimate() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 2), true);

        // Step 1
        step("Нажмите на ручку в верхней правой части экрана");
        EstimatePage estimatePage = new EstimatePage();
        OrderAppData estimateData = estimatePage.getOrderDataFromPage();
        estimatePage.clickEditEstimateButton()
                .shouldEditModeOn();

        // Step 2
        step("Нажмите на мини-карточку любого товара в списке товаров сметы");
        EstimateActionWithProductCardModal actionWithProductCardModalPage = estimatePage.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 3
        step("Выберите параметр Удалить товар");
        actionWithProductCardModalPage.clickRemoveProductMenuItem();
        ConfirmRemovingProductModal modal = new ConfirmRemovingProductModal();
        modal.verifyRequiredElements();

        // Step 4
        step("Нажмите на Удалить");
        modal.clickConfirmButton();
        estimatePage = new EstimatePage().verifyRequiredElements(
                EstimatePage.PageState.builder()
                        .productIsAdded(true)
                        .customerIsSelected(true)
                        .confirmed(true)
                        .editModeOn(true)
                        .build());

        estimateData.removeProduct(0);
        estimateData.setTotalWeight(null);
        estimatePage.shouldOrderDataIs(estimateData);
    }

    @Test(description = "C22797088 Добавление товара в смету в количестве большем, чем доступно для продажи",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testAddProductInEstimateMoreThanAvailable() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 1), false);

        // Step 1
        step("Нажмите на кнопку +товары и услуги");
        EstimatePage estimatePage = new EstimatePage();
        OrderAppData estimateData = estimatePage.getOrderDataFromPage();
        SearchProductPage searchProductPage = estimatePage.clickAddProductButton()
                .verifyRequiredElements();

        // Step 2
        step("Введите ЛМ код товара или название товара или отсканируйте товар");
        searchProductPage.enterTextInSearchFieldAndSubmit(productLmCodes.get(1));
        AddProduct35Page<EstimatePage> addProduct35Page = new AddProduct35Page<>(EstimatePage.class)
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_ESTIMATE);

        // Step 3
        step("Измените количество товара на число, большее доступного для заказа");
        int totalAvailableQuantity = addProduct35Page.getAvailableQuantityInShoppingRoom() +
                addProduct35Page.getAvailableQuantityInStock();
        String editQuantity = String.valueOf(totalAvailableQuantity + 10);
        addProduct35Page.enterQuantityOfProduct(editQuantity, true);

        // Step 4
        step("Нажмите на Добавить в смету");
        ProductOrderCardAppData newProduct = addProduct35Page.getProductOrderDataFromPage();
        estimatePage = addProduct35Page.clickAddIntoEstimateButton();
        estimatePage.verifyRequiredElements(
                EstimatePage.PageState.builder()
                        .productIsAdded(true)
                        .customerIsSelected(true)
                        .build());

        estimateData.addFirstProduct(newProduct);
        estimateData.setTotalWeight(null);
        estimatePage.shouldOrderDataIs(estimateData);
    }

    @Test(description = "C22797110 Добавить существующий товар еще раз (из модалки действий с товаром)",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testAddProductInEstimateFromActionWithProductsModal() throws Exception {
        startFromScreenWithCreatedEstimate(productLmCodes.subList(0, 1), false);

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров сметы");
        EstimatePage estimatePage = new EstimatePage();
        OrderAppData estimateData = estimatePage.getOrderDataFromPage();
        EstimateActionWithProductCardModal actionWithProductCardModalPage = estimatePage.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        step("Выберите параметр Добавить товар еще раз");
        AddProduct35Page<EstimatePage> addProduct35Page = actionWithProductCardModalPage.clickAddProductAgainMenuItem()
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_ESTIMATE);

        // Step 3
        step("Измените количество товара");
        String quantityForProduct2 = "3";
        addProduct35Page.enterQuantityOfProduct(quantityForProduct2, true);

        // Step 4
        step("Нажмите на Добавить в смету");
        ProductOrderCardAppData newProduct = addProduct35Page.getProductOrderDataFromPage();
        newProduct.setAvailableTodayQuantity(null);
        estimatePage = addProduct35Page.clickAddIntoEstimateButton();
        estimatePage.verifyRequiredElements(
                EstimatePage.PageState.builder()
                        .productIsAdded(true)
                        .customerIsSelected(true)
                        .build());

        estimateData.addFirstProduct(newProduct);
        estimateData.setTotalWeight(ParserUtil.multiply(estimateData.getTotalWeight(), 4, 2));
        estimatePage.shouldOrderDataIs(estimateData);
    }

}
