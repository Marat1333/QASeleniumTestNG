package com.leroy.magmobile.ui.tests.sales;

import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.ui.models.sales.DocumentDetailsData;
import com.leroy.magmobile.ui.models.sales.ShortSalesDocumentData;
import com.leroy.magmobile.ui.models.search.ProductCardData;
import com.leroy.magmobile.ui.pages.sales.*;
import com.leroy.magmobile.ui.pages.sales.orders.basket.*;
import com.leroy.magmobile.ui.pages.sales.product_and_service.AddServicePage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.*;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.OrderPage;
import com.leroy.magmobile.ui.pages.work.StockProductCardPage;
import com.leroy.magmobile.ui.pages.work.StockProductsPage;
import com.leroy.magmobile.ui.pages.work.modal.QuantityProductsForWithdrawalModalPage;
import com.leroy.utils.ParserUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.Random;

public class MultiFunctionalButtonTest extends SalesBaseTest {

    private final String OLD_SHOP_GROUP = "old_shop";

    @BeforeGroups(OLD_SHOP_GROUP)
    public void setSessionDataForOldShop() {
        getUserSessionData().setUserShopId(EnvConstants.SHOP_WITH_OLD_INTERFACE);
    }

    @Test(description = "C3201023 Создание документа продажи", groups = OLD_SHOP_GROUP)
    public void testC3201023() throws Exception {
        testCreateSalesDocument(getAnyLmCodeProductWithoutSpecificOptions(), ProductTypes.NORMAL);
    }

    @Test(description = "C22846947 Создание документа продажи с товаром AVS", groups = OLD_SHOP_GROUP)
    public void testC22846947() throws Exception {
        testCreateSalesDocument(getAnyLmCodeProductWithAvs(), ProductTypes.AVS);
    }

    @Test(description = "C22846948 Создание документа продажи с товаром Топ-EM", groups = OLD_SHOP_GROUP)
    public void testC22846948() throws Exception {
        testCreateSalesDocument(getAnyLmCodeProductWithTopEM(), ProductTypes.TOP_EM);
    }

    @Test(description = "C3201049 Создание документа продажи из карточки услуги")
    public void testC3201049() throws Exception {
        // Pre-condition
        String lmCode = getAnyLmCodeOfService();
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);

        // Step #1
        step("Нажмите в поле поиска");
        SearchProductPage searchPage = mainProductAndServicesPage.clickSearchBar(false)
                .verifyRequiredElements();

        // Step #2
        step("Введите ЛМ код товара (напр., " + lmCode + ")");
        searchPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddServicePage addServicePage = new AddServicePage().verifyRequiredElements()
                .shouldFieldsAre("", AddServicePage.Constants.DEFAULT_QUANTITY_VALUE,
                        AddServicePage.Constants.EMPTY_TOTAL_PRICE_VALUE);

        // Step #3
        step("Нажмите на поле Цена за единицу услуги и введите значение цены");
        String testPrice = RandomStringUtils.randomNumeric(3);
        addServicePage.enterValueInPriceServiceField(testPrice)
                .shouldFieldsAre(testPrice, AddServicePage.Constants.DEFAULT_QUANTITY_VALUE,
                        testPrice);

        // Step #4
        step("Нажмите на поле Количество для продажи и введите новое значение количества");
        String testQuantity = String.valueOf(new Random().nextInt(9) + 1);
        addServicePage.enterValueInQuantityServiceField(testQuantity)
                .shouldFieldsAre(testPrice, testQuantity,
                        String.valueOf(Integer.parseInt(testPrice) * Integer.parseInt(testQuantity)));

        // Step #5
        step("Нажмите на кнопку Добавить в документ продажи.");
        BasketStep1Page basketStep1Page = addServicePage.clickAddIntoDocumentSalesButton()
                .verifyRequiredElements();
        String documentNumber = basketStep1Page.getDocumentNumber();

        // Step #6
        step("Нажмите Далее к параметрам");
        BasketStep2Page basketStep2Page = basketStep1Page.clickNextParametersButton()
                .verifyRequiredElements()
                .shouldFieldsHaveDefaultValues();

        // Step #7
        step("Нажмите на кнопку Создать документ продажи");
        String testPinCode = getValidPinCode();
        BasketStep3Page basketStep3Page = basketStep2Page.clickCreateSalesDocumentButton()
                .verifyRequiredElements();

        // Step #8
        step("Введите пятизначный PIN-код, не использованный ранее");
        basketStep3Page.enterPinCode(testPinCode)
                .shouldPinCodeFieldIs(testPinCode)
                .shouldSubmitButtonIsActive();

        // Step #9
        step("Нажмите кнопку Подтвердить");
        SubmittedSalesDocumentPage submittedSalesDocumentPage = basketStep3Page.clickSubmitButton();
        submittedSalesDocumentPage.verifyRequiredElements()
                .shouldPinCodeIs(testPinCode)
                .shouldDocumentNumberIs(documentNumber);
    }

    @Test(description = "C3201024 Добавление в существующий документ продажи", groups = OLD_SHOP_GROUP)
    public void testC3201024() throws Exception {
        // Pre-condition
        // - Имеются документы продажи в статусе черновик
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions();
        String documentNumber = loginInAndCreateDraftSalesDocument(lmCode);

        // Steps 1, 2, 3
        ActionWithProductModalPage actionWithProductModalPage =
                testSearchForProductAndClickActionsWithProductButton(
                        null, new ProductCardData(lmCode), ProductTypes.NORMAL);

        // Step #4
        step("Нажмите Добавить в документ продажи");
        actionWithProductModalPage.clickAddIntoSalesDocumentButton();
        AddIntoSalesDocumentModalPage modalPage = new AddIntoSalesDocumentModalPage()
                .verifyRequiredElements();

        // Step #5
        step("Нажмите на любой элемент списка документов продажи");
        AddProductPage addProductPage = modalPage.selectDraftWithNumber(documentNumber)
                .verifyRequiredElements();

        // Step #6
        step("Нажмите Добавить");
        BasketStep1Page basketStep1Page = addProductPage.clickAddButton();
        basketStep1Page.verifyRequiredElements()
                .shouldDocumentNumberIs(documentNumber)
                .shouldLmCodeOfProductIs(lmCode);
    }

    @Test(description = "C22744177 Создание заявки на Отзыв RM", groups = OLD_SHOP_GROUP)
    public void testCreateOrderForWithdrawalFromRM() throws Exception {
        // Pre-condition
        String lmCode = getAnyLmCodeProductIsAvailableForWithdrawalFromRM();
        MainProductAndServicesPage mainProductAndServicesPage = loginSelectShopAndGoTo(
                MainProductAndServicesPage.class);

        // Steps 1, 2, 3
        ActionWithProductModalPage actionWithProductModalPage =
                testSearchForProductAndClickActionsWithProductButton(mainProductAndServicesPage, new ProductCardData(lmCode),
                        ProductTypes.NORMAL);

        // Step 4
        step("Нажмите на кнопку Добавить в заявку на Отзыв с RM");
        actionWithProductModalPage.clickAddIntoWithdrawalOrderFromRMButton();
        StockProductCardPage stockProductCardPage = new StockProductCardPage();
        stockProductCardPage.verifyRequiredElements();

        // Step 5
        step("Нажмите на нкопку Отозвать");
        String withdrawalCountItems = String.valueOf(new Random().nextInt(10) + 1);
        QuantityProductsForWithdrawalModalPage modalPage = stockProductCardPage.clickWithdrawalBtnForEnterQuantity()
                .verifyRequiredElements()
                .shouldSubmitButtonActivityIs(false);

        // Step 6
        step("Ввести значение количества товара на отзыв");
        modalPage.enterCountOfItems(withdrawalCountItems)
                .shouldWithdrawalButtonHasQuantity(withdrawalCountItems)
                .shouldSubmitButtonActivityIs(true);

        // Step 7
        step("Нажать на кнопку Отозвать");
        StockProductsPage stockProductsPage = modalPage.clickSubmitBtn()
                .verifyRequiredElements()
                .shouldCountOfSelectedProductsIs(1);

        // Step 8
        step("Нажмите Далее к параметрам заявки");
        OrderPage orderPage = stockProductsPage.clickSubmitBtn()
                .verifyRequiredElements()
                .shouldFieldsAreNotEmptyExceptCommentField();
        String orderNumber = orderPage.getOrderNumber();

        // Step 9
        step("Нажмите кнопку Отправить заявку");
        orderPage.clickSubmitBtn()
                .verifyRequiredElements()
                .clickSubmitBtn()
                .shouldOrderByIndexIs(1, orderNumber, null, "Создана");
    }

    @Test(description = "C22847027 35 магазин - создание заказа")
    public void test35ShopCreatingOrder() throws Exception {
        // Pre-condition
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions();
        MainProductAndServicesPage mainProductAndServicesPage = loginSelectShopAndGoTo(
                MainProductAndServicesPage.class);

        // Steps 1, 2, 3
        ProductCardData productData = new ProductCardData(lmCode);
        productData.setHasAvailableStock(false);
        ActionWithProduct35ModalPage actionWithProductModalPage =
                testSearchForProductAndClickActionsWithProductButton(
                        mainProductAndServicesPage, productData, ProductTypes.NORMAL, true);

        // Step #4
        step("Нажмите Оформить продажу");
        SaleTypeModalPage modalPage = actionWithProductModalPage.clickMakeSaleButton()
                .verifyRequiredElementsWhenFromProductCard(false);

        // Step #5
        step("Нажмите Корзина");
        AddProduct35Page addProduct35Page = modalPage.clickBasketMenuItem();
        addProduct35Page.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);
        String expectedTotalPrice = ParserUtil.strWithOnlyDigits(addProduct35Page.getPrice());

        // Step #6
        step("Нажмите Добавить в корзину");
        Basket35Page basket35Page = addProduct35Page.clickAddIntoBasketButton()
                .verifyRequiredElements(new Basket35Page.PageState().setProductIsAdded(true));

        // Step #7
        step("Нажмите Оформить");
        ProcessOrder35Page processOrder35Page = basket35Page.clickMakeSalesButton()
                .verifyRequiredElements();

        // Step #8
        step("Заполните поля Имя и Фамилия, Телефон, PIN-код для оплаты");
        DocumentDetailsData documentDetailsData = new DocumentDetailsData().setRequiredRandomData();
        documentDetailsData.setPinCode(getValidPinCode());
        documentDetailsData.setDeliveryType(DocumentDetailsData.DeliveryType.PICKUP);
        processOrder35Page.fillInFormFields(documentDetailsData)
                .shouldFormFieldsAre(documentDetailsData);

        // Step #9
        step("Нажмите на кнопку Подтвердить заказ");
        SubmittedSalesDocument35Page submittedDocument35Page = processOrder35Page.clickSubmitButton()
                .verifyRequiredElements()
                .shouldPinCodeIs(documentDetailsData.getPinCode());
        String documentNumber = submittedDocument35Page.getDocumentNumber(true);

        // Step #10
        step("Нажмите на кнопку Перейти в список документов");
        ShortSalesDocumentData expectedSalesDocument = new ShortSalesDocumentData();
        expectedSalesDocument.setDocumentTotalPrice(expectedTotalPrice);

        expectedSalesDocument.setDocumentState(SalesDocumentsConst.States.IN_PROGRESS.getUiVal());
        expectedSalesDocument.setTitle(documentDetailsData.getDeliveryType().getValue());
        expectedSalesDocument.setNumber(documentNumber);
        SalesDocumentsPage salesDocumentsPage = submittedDocument35Page
                .clickSubmitButton()
                .shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);

        // Additional Step
        step("(Доп шаг) Уходим со страницы и возвращаемся обратно");
        expectedSalesDocument.setPin(documentDetailsData.getPinCode());
        expectedSalesDocument.setDocumentState(null);
        salesDocumentsPage.clickBackButton();
        mainProductAndServicesPage = new MainProductAndServicesPage(); // Workaround for minor #bug
        mainProductAndServicesPage.goToSalesDocumentsSection()
                .goToMySales();
        salesDocumentsPage.shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);

        // Clean up
        step("(Доп шаг) Отменяем заказ через API запрос");
        cancelOrder(documentNumber);
    }

    // ---------------------- TYPICAL TESTS FOR THIS CLASS -------------------//

    /**
     * Step 1 - Нажмите в поле поиска
     * Step 2 - Введите ЛМ код товара (напр., @param lmcode)
     * Step 3 - Нажмите на кнопку Действия с товаром
     */
    private <T extends CommonActionWithProductModalPage> T testSearchForProductAndClickActionsWithProductButton(
            MainProductAndServicesPage mainProductAndServicesPage, ProductCardData productData,
            ProductTypes productType, boolean is35Shop) throws Exception {
        // Pre-condition
        if (mainProductAndServicesPage == null) {
            mainProductAndServicesPage = loginSelectShopAndGoTo(MainProductAndServicesPage.class);
        }

        // Step #1
        step("Нажмите в поле поиска");
        SearchProductPage searchPage = mainProductAndServicesPage.clickSearchBar(false);
        searchPage.shouldKeyboardVisible();
        searchPage.verifyRequiredElements();

        // Step #2
        step("Введите ЛМ код товара (напр., " + productData.getLmCode() + ")");
        searchPage.enterTextInSearchFieldAndSubmit(productData.getLmCode());
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage()
                .verifyRequiredElements(true);

        // Step #3
        step("Нажмите на кнопку Действия с товаром");
        productDescriptionPage.clickActionWithProductButton();
        if (is35Shop) {
            ActionWithProduct35ModalPage modalPage = new ActionWithProduct35ModalPage();
            return (T) modalPage.verifyRequiredElements(productData.getHasAvailableStock(),
                    productType.equals(ProductTypes.AVS));
        } else {
            ActionWithProductModalPage modalPage = new ActionWithProductModalPage();
            return (T) modalPage.verifyRequiredElements(productType.equals(ProductTypes.AVS));
        }
    }

    private <T extends CommonActionWithProductModalPage> T testSearchForProductAndClickActionsWithProductButton(
            MainProductAndServicesPage mainProductAndServicesPage, ProductCardData productData, ProductTypes productType) throws Exception {
        return testSearchForProductAndClickActionsWithProductButton(mainProductAndServicesPage, productData, productType, false);
    }

    private void testCreateSalesDocument(String lmCode, ProductTypes productType) throws Exception {

        // Steps 1, 2, 3
        ActionWithProductModalPage actionWithProductModalPage =
                testSearchForProductAndClickActionsWithProductButton(null, new ProductCardData(lmCode),
                        productType);

        // Step #4
        step("Нажмите Добавить в документ продажи");
        // Если продукт имеет опцию Топ-ЕМ, тогда невозможно оформить документ продажи по нему
        if (productType.equals(ProductTypes.TOP_EM)) {
            actionWithProductModalPage.clickAddIntoSalesDocumentButton();
            ImpossibleCreateDocumentWithTopEmModalPage modalScreen =
                    new ImpossibleCreateDocumentWithTopEmModalPage().verifyRequiredElements();

            // Step #5
            step("Нажмите на кнопку Понятно");
            modalScreen.clickSubmitButton()
                    .verifyRequiredElements(true);
        } else {
            AddProductPage addProductPage = actionWithProductModalPage.startToCreateSalesDocument()
                    .verifyRequiredElements();

            // Step #5
            step("Нажмите Добавить");
            BasketStep1Page basketStep1Page = addProductPage.clickAddButton()
                    .verifyRequiredElements();
            basketStep1Page.shouldDocumentTypeIs(BasketPage.Constants.DRAFT_DOCUMENT_TYPE);
            String documentNumber = basketStep1Page.getDocumentNumber();

            // Step #6
            step("Нажмите Далее к параметрам");
            BasketStep2Page basketStep2Page = basketStep1Page.clickNextParametersButton()
                    .verifyRequiredElements()
                    .shouldFieldsHaveDefaultValues();

            // Step #7
            step("Нажмите кнопку Создать документ продажи");
            BasketStep3Page basketStep3Page = basketStep2Page.clickCreateSalesDocumentButton()
                    .verifyRequiredElements();

            // Step #8
            step("Введите пятизначный PIN-код, не использованный ранее");
            String testPinCode = getValidPinCode();
            basketStep3Page.enterPinCode(testPinCode)
                    .shouldPinCodeFieldIs(testPinCode)
                    .shouldSubmitButtonIsActive();

            // Step #9
            step("Нажмите кнопку Подтвердить");
            basketStep3Page.clickSubmitButton()
                    .verifyRequiredElements()
                    .shouldPinCodeIs(testPinCode)
                    .shouldDocumentNumberIs(documentNumber);

            // Clean up
            // TODO Надо тут? Если надо, то вроде нужен другой API клиент
            //apiClient.cancelOrder(EnvConstants.BASIC_USER_NAME, documentNumber);
        }
    }
}