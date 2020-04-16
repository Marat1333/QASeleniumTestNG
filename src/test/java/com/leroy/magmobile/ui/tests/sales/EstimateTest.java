package com.leroy.magmobile.ui.tests.sales;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.ui.models.CustomerData;
import com.leroy.magmobile.ui.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.models.sales.SalesOrderCardData;
import com.leroy.magmobile.ui.models.sales.SalesOrderData;
import com.leroy.magmobile.ui.models.search.ProductCardData;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.EditProduct35Page;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.basket.Basket35Page;
import com.leroy.magmobile.ui.pages.sales.estimate.ActionWithProductCardModalPage;
import com.leroy.magmobile.ui.pages.sales.estimate.ActionsWithEstimateModalPage;
import com.leroy.magmobile.ui.pages.sales.estimate.EstimatePage;
import com.leroy.magmobile.ui.pages.sales.estimate.EstimateSubmittedPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.search.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import org.testng.annotations.Test;

import java.util.Random;

public class EstimateTest extends SalesBaseTest {

    @Test(description = "C22797068 Создать смету с экрана Документы продажи")
    public void testCreatingEstimateFromSalesDocumentsScreen() throws Exception {
        // Test data
        String existedClientPhone = "1111111111";
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
                .enterTextInSearchField(existedClientPhone)
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
        if (!EstimatePage.isThisPage(context)) {
            String estimateId = clientProvider.createDraftEstimateAndGetCartId();
            MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                    MainSalesDocumentsPage.class);
            SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(estimateId);
        }

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
        if (!EstimatePage.isThisPage(context)) {
            String estimateId = clientProvider.createDraftEstimateAndGetCartId();
            MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                    MainSalesDocumentsPage.class);
            SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(estimateId);
        }

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
        if (!EstimatePage.isThisPage(context)) {
            String estimateId = clientProvider.createConfirmedEstimateAndGetCartId();
            MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                    MainSalesDocumentsPage.class);
            SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(estimateId);
        }

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

}
