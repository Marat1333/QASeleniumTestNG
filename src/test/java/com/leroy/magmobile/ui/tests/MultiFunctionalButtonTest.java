package com.leroy.magmobile.ui.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.common.SearchProductPage;
import com.leroy.magmobile.ui.pages.sales.*;
import com.leroy.magmobile.ui.pages.sales.basket.*;
import com.leroy.magmobile.ui.pages.sales.product_and_service.AddServicePage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.*;
import com.leroy.magmobile.ui.pages.work.OrderPage;
import com.leroy.magmobile.ui.pages.work.StockProductCardPage;
import com.leroy.magmobile.ui.pages.work.StockProductsPage;
import com.leroy.magmobile.ui.pages.work.modal.QuantityProductsForWithdrawalModalPage;
import com.leroy.models.OrderDetailsData;
import com.leroy.models.ProductCardData;
import com.leroy.models.SalesDocumentData;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.BaseModule;
import ru.leroymerlin.qa.core.clients.magmobile.MagMobileClient;
import ru.leroymerlin.qa.core.clients.magmobile.data.ProductItemResponse;
import ru.leroymerlin.qa.core.clients.magmobile.requests.GetCatalogSearch;

import java.util.List;


@Guice(modules = {BaseModule.class})
public class MultiFunctionalButtonTest extends AppBaseSteps {

    @Inject
    private MagMobileClient apiClient;

    // Получить ЛМ код для услуги
    private String getAnyLmCodeOfService() {
        return "49055102";
    }

    // Получить ЛМ код для обычного продукта без специфичных опций
    private String getAnyLmCodeProductWithoutSpecificOptions(String shopId, Boolean hasAvailableStock) {
        if (shopId == null) // TODO может быть shopId null или нет?
            shopId = "2";
        GetCatalogSearch params = new GetCatalogSearch()
                .setShopId(shopId)
                .setTopEM(false)
                .setHasAvailableStock(hasAvailableStock);
        List<ProductItemResponse> items = apiClient.searchProductsBy(params).asJson().getItems();
        for (ProductItemResponse item : items) {
            if (item.getAvsDate() == null)
                return item.getLmCode();
        }
        if (!hasAvailableStock)
            return "13452305";
        else
            return "";
    }

    // Получить ЛМ код для продукта с AVS
    private String getAnyLmCodeProductWithAvs() {
        GetCatalogSearch params = new GetCatalogSearch()
                .setTopEM(false);
        List<ProductItemResponse> items = apiClient.searchProductsBy(params).asJson().getItems();
        for (ProductItemResponse item : items) {
            if (item.getAvsDate() != null)
                return item.getLmCode();
        }
        return "82014172";
    }

    // Получить ЛМ код для продукта с опцией TopEM
    private String getAnyLmCodeProductWithTopEM() {
        GetCatalogSearch params = new GetCatalogSearch()
                .setTopEM(true)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID);
        List<ProductItemResponse> items = apiClient.searchProductsBy(params).asJson().getItems();
        for (ProductItemResponse item : items) {
            if (item.getAvsDate() == null)
                return item.getLmCode();
        }
        if (items.size() > 0)
            return items.get(0).getLmCode();
        return "82138074";
    }

    // Получить ЛМ код для продукта, доступного для отзыва с RM
    private String getAnyLmCodeProductIsAvailableForWithdrawalFromRM() {
        return "82001470";
    }

    public String getValidPinCode() {
        return RandomStringUtils.randomNumeric(5);
    }

    // Product Types
    private enum ProductTypes {
        NORMAL, AVS, TOP_EM;
    }

    @Test(description = "C3201023 Создание документа продажи")
    public void testC3201023() throws Exception {
        testCreateSalesDocument(getAnyLmCodeProductWithoutSpecificOptions(
                null, false), ProductTypes.NORMAL);
    }

    @Test(description = "C22846947 Создание документа продажи с товаром AVS")
    public void testC22846947() throws Exception {
        testCreateSalesDocument(getAnyLmCodeProductWithAvs(), ProductTypes.AVS);
    }

    @Test(description = "C22846948 Создание документа продажи с товаром Топ-EM")
    public void testC22846948() throws Exception {
        testCreateSalesDocument(getAnyLmCodeProductWithTopEM(), ProductTypes.TOP_EM);
    }

    @Test(description = "C3201049 Создание документа продажи из карточки услуги")
    public void testC3201049() throws Exception {
        // Pre-condition
        String lmCode = getAnyLmCodeOfService();
        SalesPage salesPage = loginAndGoTo(SalesPage.class);

        // Step #1
        log.step("Нажмите в поле поиска");
        SearchProductPage searchPage = salesPage.clickSearchBar(false)
                .verifyRequiredElements();

        // Step #2
        log.step("Введите ЛМ код товара (напр., " + lmCode + ")");
        searchPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddServicePage addServicePage = new AddServicePage(context).verifyRequiredElements()
                .shouldFieldsAre("", AddServicePage.Constants.DEFAULT_QUANTITY_VALUE,
                        AddServicePage.Constants.EMPTY_TOTAL_PRICE_VALUE);

        // Step #3
        log.step("Нажмите на поле Цена за единицу услуги и введите значение цены");
        String testPrice = RandomStringUtils.randomNumeric(3);
        addServicePage.enterValueInPriceServiceField(testPrice)
                .shouldFieldsAre(testPrice, AddServicePage.Constants.DEFAULT_QUANTITY_VALUE,
                        testPrice);

        // Step #4
        log.step("Нажмите на поле Количество для продажи и введите новое значение количества");
        String testQuantity = RandomStringUtils.randomNumeric(1);
        addServicePage.enterValueInQuantityServiceField(testQuantity)
                .shouldFieldsAre(testPrice, testQuantity,
                        String.valueOf(Integer.parseInt(testPrice) * Integer.parseInt(testQuantity)));

        // Step #5
        log.step("Нажмите на кнопку Добавить в документ продажи.");
        BasketStep1Page basketStep1Page = addServicePage.clickAddIntoDocumentSalesButton()
                .verifyRequiredElements();
        String documentNumber = basketStep1Page.getDocumentNumber();

        // Step #6
        log.step("Нажмите Далее к параметрам");
        BasketStep2Page basketStep2Page = basketStep1Page.clickNextParametersButton()
                .verifyRequiredElements()
                .shouldFieldsHaveDefaultValues();

        // Step #7
        log.step("Нажмите на кнопку Создать документ продажи");
        String testPinCode = getValidPinCode();
        BasketStep3Page basketStep3Page = basketStep2Page.clickCreateSalesDocumentButton()
                .verifyRequiredElements();

        // Step #8
        log.step("Введите пятизначный PIN-код, не использованный ранее");
        basketStep3Page.enterPinCode(testPinCode)
                .shouldPinCodeFieldIs(testPinCode)
                .shouldSubmitButtonIsActive();

        // Step #9
        log.step("Нажмите кнопку Подтвердить");
        SubmittedSalesDocumentPage submittedSalesDocumentPage = basketStep3Page.clickSubmitButton();
        submittedSalesDocumentPage.verifyRequiredElements()
                .shouldPinCodeIs(testPinCode)
                .shouldDocumentNumberIs(documentNumber);
    }

    @Test(description = "C3201024 Добавление в существующий документ продажи")
    public void testC3201024() throws Exception {
        // Pre-condition
        // - Имеются документы продажи в статусе черновик
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions(null, false);
        String documentNumber = loginInAndCreateDraftSalesDocument(lmCode);

        // Steps 1, 2, 3
        ActionWithProductModalPage actionWithProductModalPage =
                testSearchForProductAndClickActionsWithProductButton(null, new ProductCardData(lmCode),
                        ProductTypes.NORMAL);

        // Step #4
        log.step("Нажмите Добавить в документ продажи");
        actionWithProductModalPage.clickAddIntoSalesDocumentButton();
        AddIntoSalesDocumentModalPage modalPage = new AddIntoSalesDocumentModalPage(context)
                .verifyRequiredElements();

        // Step #5
        log.step("Нажмите на любой элемент списка документов продажи");
        AddProductPage addProductPage = modalPage.selectDraftWithNumber(documentNumber)
                .verifyRequiredElements();

        // Step #6
        log.step("Нажмите Добавить");
        BasketStep1Page basketStep1Page = addProductPage.clickAddButton();
        basketStep1Page.verifyRequiredElements()
                .shouldDocumentNumberIs(documentNumber)
                .shouldLmCodeOfProductIs(lmCode);
    }

    @Test(description = "C22744177 Создание заявки на Отзыв RM")
    public void testCreateOrderForWithdrawalFromRM() throws Exception {
        // Pre-condition
        String lmCode = getAnyLmCodeProductIsAvailableForWithdrawalFromRM();
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        salesPage = setShopAndDepartmentForUser(salesPage, "5", "15")
                .goToSales();

        // Steps 1, 2, 3
        ActionWithProductModalPage actionWithProductModalPage =
                testSearchForProductAndClickActionsWithProductButton(salesPage, new ProductCardData(lmCode),
                        ProductTypes.NORMAL);

        // Step 4
        log.step("Нажмите на кнопку Добавить в заявку на Отзыв с RM");
        actionWithProductModalPage.clickAddIntoWithdrawalOrderFromRMButton();
        StockProductCardPage stockProductCardPage = new StockProductCardPage(context);
        stockProductCardPage.verifyRequiredElements();

        // Step 5
        log.step("Нажмите на нкопку Отозвать");
        String withdrawalCountItems = RandomStringUtils.randomNumeric(1);
        QuantityProductsForWithdrawalModalPage modalPage = stockProductCardPage.clickWithdrawalBtnForEnterQuantity()
                .verifyRequiredElements()
                .shouldSubmitButtonActivityIs(false);

        // Step 6
        log.step("Ввести значение количества товара на отзыв");
        modalPage.enterCountOfItems(withdrawalCountItems)
                .shouldWithdrawalButtonHasQuantity(withdrawalCountItems)
                .shouldSubmitButtonActivityIs(true);

        // Step 7
        log.step("Нажать на кнопку Отозвать");
        StockProductsPage stockProductsPage = modalPage.clickSubmitBtn()
                .verifyRequiredElements()
                .shouldCountOfSelectedProductsIs(1);

        // Step 8
        log.step("Нажмите Далее к параметрам заявки");
        OrderPage orderPage = stockProductsPage.clickSubmitBtn()
                .verifyRequiredElements()
                .shouldFieldsAreNotEmptyExceptCommentField();
        String orderNumber = orderPage.getOrderNumber();

        // Step 9
        log.step("Нажмите кнопку Отправить заявку");
        orderPage.clickSubmitBtn()
                .verifyRequiredElements()
                .clickSubmitBtn()
                .shouldOrderByIndexIs(1, orderNumber, null, "Создана");
    }

    @Test(description = "C22847027 35 магазин - создание заказа")
    public void test35ShopCreatingOrder() throws Exception {
        // Pre-condition
        String shopId = "35";
        boolean hasAvailableStock = false; //new Random().nextInt(2) == 1; // No one product with "hasAvailableStock" on dev environment
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions(shopId, hasAvailableStock);
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        salesPage = setShopAndDepartmentForUser(salesPage, shopId, "01")
                .goToSales();

        // Steps 1, 2, 3
        ProductCardData productData = new ProductCardData(lmCode);
        productData.setHasAvailableStock(hasAvailableStock);
        ActionWithProduct35ModalPage actionWithProductModalPage =
                testSearchForProductAndClickActionsWithProductButton(
                        salesPage, productData, ProductTypes.NORMAL, true);

        // Step #4
        log.step("Нажмите Оформить продажу");
        SaleTypeModalPage modalPage = actionWithProductModalPage.clickMakeSaleButton()
                .verifyRequiredElements(false);

        // Step #5
        log.step("Нажмите Корзина");
        AddProduct35Page addProduct35Page = modalPage.clickBasketMenuItem()
                .verifyRequiredElements();
        String expectedTotalPrice = addProduct35Page.getPrice();

        // Step #6
        log.step("Нажмите Добавить в корзину");
        Basket35Page basket35Page = addProduct35Page.clickAddButton()
                .verifyRequiredElements();

        // Step #7
        log.step("Нажмите Оформить");
        ProcessOrder35Page processOrder35Page = basket35Page.clickSubmitButton()
                .verifyRequiredElements();

        // Step #8
        log.step("Заполните поля Имя и Фамилия, Телефон, PIN-код для оплаты");
        OrderDetailsData orderDetailsData = new OrderDetailsData().setRequiredRandomData();
        orderDetailsData.setDeliveryType(OrderDetailsData.DeliveryType.PICKUP);
        processOrder35Page.fillInFormFields(orderDetailsData)
                .shouldFormFieldsAre(orderDetailsData);

        // Step #9
        log.step("Нажмите на кнопку Подтвердить заказ");
        SubmittedSalesDocument35Page submittedDocument35Page = processOrder35Page.clickSubmitButton()
                .verifyRequiredElements()
                .shouldPinCodeIs(orderDetailsData.getPinCode());
        String documentNumber = submittedDocument35Page.getDocumentNumber(true);

        // Step #10
        log.step("Нажмите на кнопку Перейти в список документов");
        SalesDocumentData expectedSalesDocument = new SalesDocumentData();
        expectedSalesDocument.setPrice(expectedTotalPrice);
        expectedSalesDocument.setPin(orderDetailsData.getPinCode());
        expectedSalesDocument.setDocumentType("Автообработка");
        expectedSalesDocument.setWhereFrom(orderDetailsData.getDeliveryType().getValue());
        expectedSalesDocument.setNumber(documentNumber);
        submittedDocument35Page
                .clickSubmitButton()
                .shouldSalesDocumentByIndexIs(0, expectedSalesDocument);
    }

    // ---------------------- TYPICAL TESTS FOR THIS CLASS -------------------//

    /**
     * Step 1 - Нажмите в поле поиска
     * Step 2 - Введите ЛМ код товара (напр., @param lmcode)
     * Step 3 - Нажмите на кнопку Действия с товаром
     */
    private <T extends CommonActionWithProductModalPage> T testSearchForProductAndClickActionsWithProductButton(
            SalesPage salesPage, ProductCardData productData, ProductTypes productType, boolean is35Shop) throws Exception {
        // Pre-condition
        if (salesPage == null)
            salesPage = loginAndGoTo(LoginType.USER_WITH_OLD_INTERFACE, SalesPage.class);

        // Step #1
        log.step("Нажмите в поле поиска");
        SearchProductPage searchPage = salesPage.clickSearchBar(false);
        searchPage.shouldKeyboardVisible();
        searchPage.verifyRequiredElements();

        // Step #2
        log.step("Введите ЛМ код товара (напр., " + productData.getLmCode() + ")");
        searchPage.enterTextInSearchFieldAndSubmit(productData.getLmCode());
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage(context)
                .verifyRequiredElements(true);

        // Step #3
        log.step("Нажмите на кнопку Действия с товаром");
        productDescriptionPage.clickActionWithProductButton();
        if (is35Shop) {
            ActionWithProduct35ModalPage modalPage = new ActionWithProduct35ModalPage(context);
            return (T) modalPage.verifyRequiredElements(productData.isHasAvailableStock(),
                    productType.equals(ProductTypes.AVS));
        } else {
            ActionWithProductModalPage modalPage = new ActionWithProductModalPage(context);
            return (T) modalPage.verifyRequiredElements(productType.equals(ProductTypes.AVS));
        }
    }

    private <T extends CommonActionWithProductModalPage> T testSearchForProductAndClickActionsWithProductButton(
            SalesPage salesPage, ProductCardData productData, ProductTypes productType) throws Exception {
        return testSearchForProductAndClickActionsWithProductButton(salesPage, productData, productType, false);
    }

    private void testCreateSalesDocument(String lmCode, ProductTypes productType) throws Exception {

        // Steps 1, 2, 3
        ActionWithProductModalPage actionWithProductModalPage =
                testSearchForProductAndClickActionsWithProductButton(null, new ProductCardData(lmCode),
                        productType);

        // Step #4
        log.step("Нажмите Добавить в документ продажи");
        // Если продукт имеет опцию Топ-ЕМ, тогда невозможно оформить документ продажи по нему
        if (productType.equals(ProductTypes.TOP_EM)) {
            actionWithProductModalPage.clickAddIntoSalesDocumentButton();
            ImpossibleCreateDocumentWithTopEmModalPage modalScreen =
                    new ImpossibleCreateDocumentWithTopEmModalPage(context).verifyRequiredElements();

            // Step #5
            log.step("Нажмите на кнопку Понятно");
            modalScreen.clickSubmitButton()
                    .verifyRequiredElements(true);
        } else {
            AddProductPage addProductPage = actionWithProductModalPage.startToCreateSalesDocument()
                    .verifyRequiredElements();

            // Step #5
            log.step("Нажмите Добавить");
            BasketStep1Page basketStep1Page = addProductPage.clickAddButton()
                    .verifyRequiredElements();
            basketStep1Page.shouldDocumentTypeIs(BasketPage.Constants.DRAFT_DOCUMENT_TYPE);
            String documentNumber = basketStep1Page.getDocumentNumber();

            // Step #6
            log.step("Нажмите Далее к параметрам");
            BasketStep2Page basketStep2Page = basketStep1Page.clickNextParametersButton()
                    .verifyRequiredElements()
                    .shouldFieldsHaveDefaultValues();

            // Step #7
            log.step("Нажмите кнопку Создать документ продажи");
            BasketStep3Page basketStep3Page = basketStep2Page.clickCreateSalesDocumentButton()
                    .verifyRequiredElements();

            // Step #8
            log.step("Введите пятизначный PIN-код, не использованный ранее");
            String testPinCode = RandomStringUtils.randomNumeric(5);
            basketStep3Page.enterPinCode(testPinCode)
                    .shouldPinCodeFieldIs(testPinCode)
                    .shouldSubmitButtonIsActive();

            // Step #9
            log.step("Нажмите кнопку Подтвердить");
            basketStep3Page.clickSubmitButton()
                    .verifyRequiredElements()
                    .shouldPinCodeIs(testPinCode)
                    .shouldDocumentNumberIs(documentNumber);
        }
    }
}