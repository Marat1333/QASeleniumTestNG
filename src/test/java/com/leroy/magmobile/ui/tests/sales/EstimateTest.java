package com.leroy.magmobile.ui.tests.sales;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.ui.models.CustomerData;
import com.leroy.magmobile.ui.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.models.sales.SalesOrderCardData;
import com.leroy.magmobile.ui.models.sales.SalesOrderData;
import com.leroy.magmobile.ui.models.search.ProductCardData;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.customers.EditCustomerContactDetailsPage;
import com.leroy.magmobile.ui.pages.customers.EditCustomerInfoPage;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.EditProduct35Page;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.basket.Basket35Page;
import com.leroy.magmobile.ui.pages.sales.estimate.*;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import java.util.Random;

public class EstimateTest extends SalesBaseTest {

    private String firstCustomerPhone = "1111111111";
    private String secondCustomerPhone = "2222222222";

    @Step("Pre-condition: Создание сметы")
    private void startFromScreenWithCreatedEstimate(int productCountInEstimate, boolean isConfirmed) throws Exception {
        boolean byApi = true; // TODO should be true
        if (!EstimatePage.isThisPage(context)) {
            if (byApi) {
                String estimateId = isConfirmed ? clientProvider.createConfirmedEstimateAndGetCartId() :
                        clientProvider.createDraftEstimateAndGetCartId(productCountInEstimate);
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
                AddProduct35Page addProduct35Page = new AddProduct35Page(context);
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
        log.step("Нажать кнопку Оформить продажу");
        SaleTypeModalPage modalPage = mainSalesDocumentsPage.clickCreateSalesDocumentButton();
        modalPage.verifyRequiredElementsWhenFromSalesDocuments();

        // Step 2
        log.step("Выбрать параметр Смета");
        EstimatePage estimatePage = modalPage.clickEstimateMenuItem();
        EstimatePage.PageState pageState = new EstimatePage.PageState()
                .setCustomerIsSelected(false).setProductIsAdded(false);
        estimatePage.verifyRequiredElements(pageState);

        // Step 3
        log.step("Нажмите на поле Клиенты");
        SearchCustomerPage searchCustomerPage = estimatePage.clickCustomerField();
        searchCustomerPage.verifyRequiredElements();

        // Step 4
        log.step("Введите номер телефона/ №карты клиента/ эл. почту");
        CustomerData customerData = searchCustomerPage.selectSearchType(SearchCustomerPage.SearchType.BY_PHONE)
                .enterTextInSearchField(firstCustomerPhone)
                .getCustomerDataFromSearchListByIndex(1);
        estimatePage = searchCustomerPage.selectCustomerFromSearchList(1);
        pageState = new EstimatePage.PageState()
                .setCustomerIsSelected(true).setProductIsAdded(false);
        estimatePage.verifyRequiredElements(pageState);
        estimatePage.shouldSelectedCustomerIs(customerData);

        // Step 5
        log.step("Нажмите на кнопку +товары и услуги");
        SearchProductPage searchProductPage = estimatePage.clickProductAndServiceButton();
        searchProductPage.verifyRequiredElements();

        // Step 6
        log.step("Введите ЛМ код товара или название товара или отсканируйте товар");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page addProduct35Page = new AddProduct35Page(context);
        addProduct35Page.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_ESTIMATE);

        // Step 7
        log.step("Нажмите на Добавить в смету");
        estimatePage = addProduct35Page.clickAddIntoEstimateButton();
        pageState = new EstimatePage.PageState()
                .setCustomerIsSelected(true).setProductIsAdded(true);
        estimatePage.verifyRequiredElements(pageState);

        // Step 8
        log.step("Нажмите на Создать");
        String expectedTotalPrice = estimatePage.getTotalPrice();
        String documentNumber = estimatePage.getDocumentNumber(true);
        EstimateSubmittedPage estimateSubmittedPage = estimatePage.clickCreateButton()
                .verifyRequiredElements();

        // Step 9
        log.step("Нажать на Перейти в список документов");
        SalesDocumentData expectedSalesDocument = new SalesDocumentData();
        expectedSalesDocument.setPrice(expectedTotalPrice);
        expectedSalesDocument.setDocumentState(SalesDocumentsConst.States.CONFIRMED.getUiVal());
        expectedSalesDocument.setTitle(SalesDocumentsConst.Types.QUOTATION.getUiVal());
        expectedSalesDocument.setNumber(documentNumber);
        SalesDocumentsPage salesDocumentsPage = estimateSubmittedPage.clickSubmitButton();
        salesDocumentsPage.verifyRequiredElements()
                .shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);
        // Post actions
        log.step("(Доп. шаг) Найти и открыть созданную смету");
        salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                expectedSalesDocument.getNumber());
        new EstimatePage(context);
    }

    @Test(description = "C22797074 Посмотреть подробнее о товаре")
    public void testViewProductDetailsFromEstimateScreen() throws Exception {
        startFromScreenWithCreatedEstimate(1, false);

        EstimatePage estimatePage = new EstimatePage(context);
        ProductCardData productCardData = estimatePage.getCardDataListFromPage()
                .get(0).getProductCardData();

        // Step 1
        log.step("Нажмите на мини-карточку любого товара в списке товаров сметы");
        ActionWithProductCardModalPage modalPage = estimatePage.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        log.step("Выберите параметр Подробнее о товаре");
        ProductDescriptionPage productDescriptionPage = modalPage.clickProductDetailsMenuItem();
        productDescriptionPage.verifyRequiredElements(false)
                .shouldProductLMCodeIs(productCardData.getLmCode());
    }

    @Test(description = "C22797073 Изменить количество добавленного товара")
    public void testChangeProductQuantityFromEstimateScreen() throws Exception {
        startFromScreenWithCreatedEstimate(1, false);

        EstimatePage estimatePage = new EstimatePage(context);
        ProductCardData productCardDataBefore = estimatePage.getCardDataListFromPage()
                .get(0).getProductCardData(); // TODO мы должны из API брать эти данные

        // Step 1
        log.step("Нажмите на мини-карточку любого товара в списке товаров сметы");
        ActionWithProductCardModalPage modalPage = estimatePage.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        log.step("Выберите параметр Изменить количество");
        EditProduct35Page editProduct35Page = modalPage.clickChangeQuantityMenuItem();
        editProduct35Page.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.SAVE);

        // Step 3
        log.step("Измените количество товара");
        String testEditQuantityValue = String.valueOf(new Random().nextInt(10) + 1);
        Double expectedTotalCost = Double.parseDouble(testEditQuantityValue) * productCardDataBefore.getPrice();
        editProduct35Page.enterQuantityOfProduct(testEditQuantityValue)
                .shouldEditQuantityFieldIs(testEditQuantityValue)
                .shouldTotalPriceCalculateCorrectly();

        // Step 4
        log.step("Нажмите на кнопку Сохранить");
        estimatePage = editProduct35Page.clickSaveButton();
        SalesOrderCardData productCardDataAfter = estimatePage.getCardDataListFromPage()
                .get(0); // TODO Заменить на ProductOrderData

        softAssert.isEquals(productCardDataAfter.getProductCardData().getPrice(),
                productCardDataBefore.getPrice(),
                "Цена товара изменилась");
        softAssert.isEquals(productCardDataAfter.getTotalPrice(), expectedTotalCost,
                "Неверная сумма для выбранного товара");
        softAssert.isEquals(productCardDataAfter.getSelectedQuantity(), Double.parseDouble(testEditQuantityValue),
                "Неверное выбранное кол-во товара");
        softAssert.verifyAll();
    }

    @Test(description = "C22797078 Преобразовать смету в корзину")
    public void testTransformEstimateToBasket() throws Exception {
        startFromScreenWithCreatedEstimate(1, true);

        EstimatePage estimatePage = new EstimatePage(context);
        // Collect test data from page
        SalesOrderData testEstimateData = estimatePage.getEstimateDataFromPage();

        // Step 1
        log.step("Нажмите на кнопку Действия со сметой");
        ActionsWithEstimateModalPage modalPage = estimatePage.clickActionsWithEstimateButton();
        modalPage.verifyRequiredElements();

        // Step 2
        log.step("Выберите параметр Преобразовать в корзину");
        Basket35Page basket35Page = modalPage.clickTransformToBasketMenuItem();
        basket35Page.verifyRequiredElements(new Basket35Page.PageState().setProductIsAdded(true));
        basket35Page.shouldOrderDataIs(testEstimateData);
    }

    @Test(description = "C22797070 Добавить существующий товар еще раз (из поиска)")
    public void testAddProductIntoEstimateAgain() throws Exception {
        startFromScreenWithCreatedEstimate(1, false);

        // Step 1
        step("Нажмите на кнопку +товары и услуги");
        EstimatePage estimatePage = new EstimatePage(context);
        SalesOrderData firstEstimateStateData = estimatePage.getEstimateDataFromPage();
        SalesOrderCardData firstProductInEstimate = firstEstimateStateData.getOrderCardDataList().get(0);
        SearchProductPage searchProductPage = estimatePage.clickAddProductButton();
        searchProductPage.verifyRequiredElements();

        // Step 2
        step("Введите ЛМ код товара или название товара или отсканируйте товар, ранее добавленный в смету");
        searchProductPage.searchProductAndSelect(firstProductInEstimate.getProductCardData().getLmCode());
        EstimateAddProductPage addProductPage = new EstimateAddProductPage(context);
        addProductPage.verifyRequiredElements();

        // Step 3
        step("Измените количество товара");
        String quantityForProduct2 = String.valueOf(Math.round(firstProductInEstimate.getSelectedQuantity()) + 1);
        addProductPage.enterQuantityOfProduct(quantityForProduct2)
                .shouldEditQuantityFieldIs(quantityForProduct2)
                .shouldTotalPriceCalculateCorrectly();

        // Step 4
        step("Нажмите на Добавить в смету");
        SalesOrderData estimateData = addProductPage.clickAddIntoEstimateButton().getEstimateDataFromPage();
        SalesOrderCardData newProduct = estimateData.getOrderCardDataList().get(0);
        softAssert.isEquals(estimateData.getProductCount(), 2, "Метка с кол-вом товара не верна");
        softAssert.isEquals(estimateData.getOrderCardDataList().size(), 2,
                "Кол-во добавленного в смету товара неверно");
        softAssert.isEquals(newProduct.getSelectedQuantity(),
                Double.parseDouble(quantityForProduct2), "Кол-во товара №2 неверно");
        softAssert.isEquals(estimateData.getTotalPrice(),
                firstProductInEstimate.getTotalPrice() +
                        newProduct.getTotalPrice(), "Сумма итого неверна");
        softAssert.isTrue(firstEstimateStateData.getTotalWeight() < estimateData.getTotalWeight(),
                "Вес должен был увеличиться после добавления нового товара");
        softAssert.verifyAll();
    }

    @Test(description = "C22797071 Удалить товар из сметы")
    public void testRemoveProductFromEstimate() throws Exception {
        startFromScreenWithCreatedEstimate(2, false);

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров сметы");
        EstimatePage estimatePage = new EstimatePage(context);
        SalesOrderData estimateDataBefore = estimatePage.getEstimateDataFromPage();
        ActionWithProductCardModalPage actionWithProductCardModalPage = estimatePage.clickCardByIndex(1);
        actionWithProductCardModalPage.verifyRequiredElements();

        // Step 2
        step("Выберите параметр Удалить товар");
        actionWithProductCardModalPage.clickRemoveProductMenuItem();
        ConfirmRemovingProductModal modal = new ConfirmRemovingProductModal(context);
        modal.verifyRequiredElements();

        // Step 3
        step("Нажмите на Удалить");
        modal.clickConfirmButton();
        estimatePage = new EstimatePage(context);
        SalesOrderData estimateDataAfter = estimatePage.getEstimateDataFromPage();
        softAssert.isEquals(estimateDataAfter.getProductCount(), 1, "Метка кол-ва товаров неверна");
        softAssert.isEquals(estimateDataAfter.getOrderCardDataList().size(), 1,
                "Ошибочное кол-во товаров на странице");
        softAssert.isTrue(estimateDataBefore.getTotalWeight() > estimateDataAfter.getTotalWeight() &&
                        estimateDataAfter.getTotalWeight() > 0,
                "Вес должен был уменьшиться");
        softAssert.isEquals(estimateDataAfter.getTotalPrice(),
                estimateDataAfter.getOrderCardDataList().get(0).getTotalPrice(),
                "Общая стоимость не пересчитана");
        softAssert.verifyAll();
    }

    @Test(description = "C22797072 Удалить последний товар из сметы")
    public void testRemoveLastProductFromEstimate() throws Exception {
        startFromScreenWithCreatedEstimate(1, false);

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров сметы");
        EstimatePage estimatePage = new EstimatePage(context);
        String estimateId = estimatePage.getDocumentNumber(true);
        ActionWithProductCardModalPage actionWithProductCardModalPage = estimatePage.clickCardByIndex(1);
        actionWithProductCardModalPage.verifyRequiredElements();

        // Step 2
        step("Выберите параметр Удалить товар");
        actionWithProductCardModalPage.clickRemoveProductMenuItem();
        ConfirmRemoveLastProductEstimateModal modal = new ConfirmRemoveLastProductEstimateModal(context);
        modal.verifyRequiredElements();

        // Step 3
        step("Нажмите на Удалить");
        modal.clickConfirmButton();
        SalesDocumentsPage salesDocumentsPage = new SalesDocumentsPage(context);
        salesDocumentsPage.shouldSalesDocumentIsNotPresent(estimateId);
    }

    @Test(description = "C22797075 Выбрать другого клиента для сметы")
    public void testEditCustomerInEstimate() throws Exception {
        startFromScreenWithCreatedEstimate(1, false);

        // Step 1
        step("Нажать на кнопку редактирования клиента");
        EstimatePage estimatePage = new EstimatePage(context);
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
        anAssert.isNotEquals(customerData.getName(), customerNameBefore,
                "В поиске мы нашли клиента с тем же именем, который был и до этого");

        // Step 4
        step("Нажмите на мини-карточку нужного клиента");
        estimatePage = searchCustomerPage.selectCustomerFromSearchList(1);
        anAssert.isEquals(estimatePage.getCustomerName(), customerData.getName(),
                "На странице отображается не тот клиент, которого выбрали");
    }

    @Test(description = "C22797076 Изменить контактные данные клиента")
    public void testChangeCustomerContactDetailsInEstimate() throws Exception {
        startFromScreenWithCreatedEstimate(1, false);

        // Step 1
        step("Нажать на кнопку редактирования клиента");
        EstimatePage estimatePage = new EstimatePage(context);
        String customerNameBefore = estimatePage.getCustomerName();
        EditCustomerModalPage modalPage = estimatePage.clickEditCustomerField();
        modalPage.verifyRequiredElements();

        // Step 2
        step("Выберете параметр Изменить контактные данные");
        EditCustomerContactDetailsPage editCustomerContactDetailsPage = modalPage.clickChangeContactDetails()
                .verifyRequiredElements();

        // Step 3
        step("Нажмите на пустое поле Телефон и Введите новый номер");
        editCustomerContactDetailsPage.fillInPhoneNumber("232323");

        // Step 4
        step("Нажмите на Сохранить");
        editCustomerContactDetailsPage.clickSaveButton();
        String s = "";
    }

}
