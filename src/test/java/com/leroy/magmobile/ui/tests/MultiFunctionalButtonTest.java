package com.leroy.magmobile.ui.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.models.CustomerData;
import com.leroy.magmobile.models.sales.OrderDetailsData;
import com.leroy.magmobile.models.sales.SalesDocumentData;
import com.leroy.magmobile.models.sales.SalesOrderCardData;
import com.leroy.magmobile.models.sales.SalesOrderData;
import com.leroy.magmobile.models.search.ProductCardData;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.helpers.SalesDocDataGenerator;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.sales.*;
import com.leroy.magmobile.ui.pages.sales.basket.*;
import com.leroy.magmobile.ui.pages.sales.estimate.ActionWithProductCardModalPage;
import com.leroy.magmobile.ui.pages.sales.estimate.ActionsWithEstimateModalPage;
import com.leroy.magmobile.ui.pages.sales.estimate.EstimatePage;
import com.leroy.magmobile.ui.pages.sales.estimate.EstimateSubmittedPage;
import com.leroy.magmobile.ui.pages.sales.product_and_service.AddServicePage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.*;
import com.leroy.magmobile.ui.pages.search.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.OrderPage;
import com.leroy.magmobile.ui.pages.work.StockProductCardPage;
import com.leroy.magmobile.ui.pages.work.StockProductsPage;
import com.leroy.magmobile.ui.pages.work.modal.QuantityProductsForWithdrawalModalPage;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.CartData;
import com.leroy.umbrella_extension.magmobile.data.ProductItemResponse;
import com.leroy.umbrella_extension.magmobile.data.estimate.EstimateData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogSearch;
import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.BaseModule;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.text.NumberFormat;
import java.util.*;


@Guice(modules = {BaseModule.class})
public class MultiFunctionalButtonTest extends AppBaseSteps {

    @Inject
    private MagMobileClient apiClient;

    @Inject
    private AuthClient authClient;

    // Получить ЛМ код для услуги
    private String getAnyLmCodeOfService() {
        return "49055102";
    }

    // Получить ЛМ код для обычного продукта без специфичных опций
    private List<String> getAnyLmCodesProductWithoutSpecificOptions(int necessaryCount, String shopId,
                                                                    Boolean hasAvailableStock) {
        String[] badLmCodes = {"10008698", "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        if (shopId == null) // TODO может быть shopId null или нет?
            shopId = "5";
        GetCatalogSearch params = new GetCatalogSearch()
                .setShopId(shopId)
                .setTopEM(false)
                .setHasAvailableStock(hasAvailableStock);
        List<ProductItemResponse> items = apiClient.searchProductsBy(params).asJson().getItems();
        List<String> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemResponse item : items) {
            if (item.getAvsDate() == null && !Arrays.asList(badLmCodes).contains(item.getLmCode())) {
                if (necessaryCount > i)
                    resultList.add(item.getLmCode());
                else
                    break;
                i++;
            }
        }
        return resultList;
    }

    private String getAnyLmCodeProductWithoutSpecificOptions(String shopId, Boolean hasAvailableStock) {
        return getAnyLmCodesProductWithoutSpecificOptions(1, shopId, hasAvailableStock).get(0);
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

    private String getValidPinCode() {
        return SalesDocDataGenerator.getAvailablePinCode(apiClient);
    }

    // CREATING PRE-CONDITIONS:

    private String createDraftEstimate() {
        String shopId = "35";
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions(shopId, false);
        String token = authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS);
        ProductOrderData productOrderData = new ProductOrderData();
        productOrderData.setLmCode(lmCode);
        productOrderData.setQuantity(1.0);
        Response<EstimateData> estimateDataResponse = apiClient
                .createEstimate(token, "35", productOrderData);
        Assert.assertTrue(estimateDataResponse.isSuccessful(),
                "Не смогли создать Смету на этапе создания pre-condition данных");
        return estimateDataResponse.asJson().getEstimateId();
    }

    private String createDraftCart(int productCount) {
        String shopId = "35";
        List<String> lmCodes = getAnyLmCodesProductWithoutSpecificOptions(productCount, shopId, false);
        String token = authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS);
        List<ProductOrderData> productOrderDataList = new ArrayList<>();
        Random r = new Random();
        for (String lmCode : lmCodes) {
            ProductOrderData productOrderData = new ProductOrderData();
            productOrderData.setLmCode(lmCode);
            productOrderData.setQuantity((double) (r.nextInt(9) + 1));
            productOrderDataList.add(productOrderData);
        }
        Response<CartData> cartDataResponse = apiClient
                .createCart(token, "35", productOrderDataList);
        Assert.assertTrue(cartDataResponse.isSuccessful(),
                "Не смогли создать Корзину на этапе создания pre-condition данных");
        return cartDataResponse.asJson().getFullDocId();
    }

    private void cancelOrder(String orderId) throws Exception {
        Response<JSONObject> r = apiClient.cancelOrder(EnvConstants.BASIC_USER_LDAP, orderId);
        if (!r.isSuccessful()) {
            Thread.sleep(10000); // TODO можно подумать над не implicit wait'ом
            Log.warn(r.toString());
            r = apiClient.cancelOrder(EnvConstants.BASIC_USER_LDAP, orderId);
        }
        anAssert.isTrue(r.isSuccessful(),
                "Не смогли удалить заказ №" + orderId + ". Ошибка: " + r.toString());
    }

    // Product Types
    private enum ProductTypes {
        NORMAL, AVS, TOP_EM;
    }

    // Документы продажи

    @Test(description = "C3201029 Создание документа продажи")
    public void testC3201029() throws Exception {
        // Step #1
        log.step("На главном экране выберите раздел Документы продажи");
        MainSalesDocumentsPage salesDocumentsPage = loginAndGoTo(LoginType.USER_WITH_OLD_INTERFACE,
                MainSalesDocumentsPage.class);
        salesDocumentsPage.verifyRequiredElements();

        // Step #2
        log.step("Нажмите 'Создать документ продажи'");
        SearchProductPage searchProductPage = salesDocumentsPage.clickCreateSalesDocumentButton();
        searchProductPage.verifyRequiredElements();

        // Step #3
        String inputDataStep3 = "164";
        log.step("Введите 164 код товара");
        searchProductPage.enterTextInSearchFieldAndSubmit(inputDataStep3)
                .shouldCountOfProductsOnPageMoreThan(1)
                .shouldProductCardsContainText(inputDataStep3)
                .shouldProductCardContainAllRequiredElements(1);

        // Step #4
        log.step("Нажмите на мини-карточку товара 16410291");
        AddProductPage addProductPage = searchProductPage.searchProductAndSelect("16410291")
                .verifyRequiredElements();

        // Step #5
        log.step("Нажмите на поле количества");
        addProductPage.clickEditQuantityField()
                .shouldKeyboardVisible();
        addProductPage.shouldEditQuantityFieldIs("1,00")
                .shouldTotalPriceIs(String.format("%.2f", Double.parseDouble(
                        addProductPage.getPrice())));

        // Step #6
        log.step("Введите значение 20,5 количества товара");
        String expectedTotalPrice = String.format("%.2f",
                Double.parseDouble(addProductPage.getPrice()) * 20.5);
        addProductPage.enterQuantityOfProduct("20,5")
                .shouldTotalPriceIs(expectedTotalPrice);

        // Step #7
        log.step("Нажмите кнопку Добавить");
        BasketStep1Page basketStep1Page = addProductPage.clickAddButton()
                .verifyRequiredElements();
        basketStep1Page.shouldDocumentTypeIs(BasketPage.Constants.DRAFT_DOCUMENT_TYPE);
        String documentNumber = basketStep1Page.getDocumentNumber();

        // Step #8
        log.step("Нажмите Далее к параметрам");
        BasketStep2Page basketStep2Page = basketStep1Page.clickNextParametersButton()
                .verifyRequiredElements()
                .shouldFieldsHaveDefaultValues();

        // Step #9
        log.step("Нажмите кнопку Создать документ продажи");
        BasketStep3Page basketStep3Page = basketStep2Page.clickCreateSalesDocumentButton()
                .verifyRequiredElements();
        basketStep3Page.shouldKeyboardVisible();

        // Step #10
        log.step("Введите 5 цифр PIN-кода");
        String testPinCode = SalesDocDataGenerator.getAvailablePinCode(apiClient);
        basketStep3Page.enterPinCode(testPinCode)
                .shouldPinCodeFieldIs(testPinCode)
                .shouldSubmitButtonIsActive();

        // Step #11
        log.step("Нажмите кнопку Подтвердить");
        SubmittedSalesDocumentPage submittedSalesDocumentPage = basketStep3Page.clickSubmitButton()
                .verifyRequiredElements()
                .shouldPinCodeIs(testPinCode)
                .shouldDocumentNumberIs(documentNumber);

        // Step #12
        log.step("Нажмите кнопку Перейти в список документов");
        SalesDocumentData expectedSalesDocument = new SalesDocumentData();
        expectedSalesDocument.setPrice(NumberFormat.getInstance(Locale.FRANCE)
                .parse(expectedTotalPrice).toString());
        expectedSalesDocument.setPin(testPinCode);
        expectedSalesDocument.setDocumentState(SalesDocumentsConst.CREATED_STATE);
        expectedSalesDocument.setTitle("Из торгового зала");
        expectedSalesDocument.setNumber(documentNumber);
        submittedSalesDocumentPage.clickSubmitButton()
                .shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);
    }

    // Мультифункциональная кнопка

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
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);

        // Step #1
        log.step("Нажмите в поле поиска");
        SearchProductPage searchPage = mainProductAndServicesPage.clickSearchBar(false)
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
        String testQuantity = String.valueOf(new Random().nextInt(9) + 1);
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
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        mainProductAndServicesPage = setShopAndDepartmentForUser(mainProductAndServicesPage, "5", "15")
                .goToSales();

        // Steps 1, 2, 3
        ActionWithProductModalPage actionWithProductModalPage =
                testSearchForProductAndClickActionsWithProductButton(mainProductAndServicesPage, new ProductCardData(lmCode),
                        ProductTypes.NORMAL);

        // Step 4
        log.step("Нажмите на кнопку Добавить в заявку на Отзыв с RM");
        actionWithProductModalPage.clickAddIntoWithdrawalOrderFromRMButton();
        StockProductCardPage stockProductCardPage = new StockProductCardPage(context);
        stockProductCardPage.verifyRequiredElements();

        // Step 5
        log.step("Нажмите на нкопку Отозвать");
        String withdrawalCountItems = String.valueOf(new Random().nextInt(10) + 1);
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
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(LoginType.USER_WITH_NEW_INTERFACE_LIKE_35_SHOP, MainProductAndServicesPage.class);

        // Steps 1, 2, 3
        ProductCardData productData = new ProductCardData(lmCode);
        productData.setHasAvailableStock(hasAvailableStock);
        ActionWithProduct35ModalPage actionWithProductModalPage =
                testSearchForProductAndClickActionsWithProductButton(
                        mainProductAndServicesPage, productData, ProductTypes.NORMAL, true);

        // Step #4
        log.step("Нажмите Оформить продажу");
        SaleTypeModalPage modalPage = actionWithProductModalPage.clickMakeSaleButton()
                .verifyRequiredElementsWhenFromProductCard(false);

        // Step #5
        log.step("Нажмите Корзина");
        AddProduct35Page addProduct35Page = modalPage.clickBasketMenuItem();
        addProduct35Page.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);
        String expectedTotalPrice = addProduct35Page.getPrice();

        // Step #6
        log.step("Нажмите Добавить в корзину");
        Basket35Page basket35Page = addProduct35Page.clickAddIntoBasketButton()
                .verifyRequiredElements(new Basket35Page.PageState().setProductIsAdded(true));

        // Step #7
        log.step("Нажмите Оформить");
        ProcessOrder35Page processOrder35Page = basket35Page.clickMakeSalesButton()
                .verifyRequiredElements();

        // Step #8
        log.step("Заполните поля Имя и Фамилия, Телефон, PIN-код для оплаты");
        OrderDetailsData orderDetailsData = new OrderDetailsData().setRequiredRandomData();
        orderDetailsData.setPinCode(getValidPinCode());
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

        expectedSalesDocument.setDocumentState(SalesDocumentsConst.AUTO_PROCESSING_STATE);
        expectedSalesDocument.setTitle(orderDetailsData.getDeliveryType().getValue());
        expectedSalesDocument.setNumber(documentNumber);
        SalesDocumentsPage salesDocumentsPage = submittedDocument35Page
                .clickSubmitButton()
                .shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);

        // Additional Step
        log.step("(Доп шаг) Уходим со страницы и возвращаемся обратно");
        expectedSalesDocument.setPin(orderDetailsData.getPinCode());
        expectedSalesDocument.setDocumentState(null);
        salesDocumentsPage.clickBackButton();
        mainProductAndServicesPage = new MainProductAndServicesPage(context); // Workaround for minor #bug
        mainProductAndServicesPage.goToSalesDocumentsSection()
                .goToMySales();
        salesDocumentsPage.shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);

        // Clean up
        log.step("(Доп шаг) Отменяем заказ через API запрос");
        cancelOrder(documentNumber);
    }

    // Смета

    @Test(description = "C22797068 Создать смету с экрана Документы продажи")
    public void testCreatingEstimateFromSalesDocumentsScreen() throws Exception {
        // Test data
        String existedClientPhone = "1111111111";
        String shopId = "35";
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions(shopId, false);
        // Pre-condition
        MainSalesDocumentsPage mainSalesDocumentsPage = loginAndGoTo(
                LoginType.USER_WITH_NEW_INTERFACE_LIKE_35_SHOP, MainSalesDocumentsPage.class);

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
        expectedSalesDocument.setDocumentState(SalesDocumentsConst.CREATED_STATE);
        expectedSalesDocument.setTitle(SalesDocumentsConst.ESTIMATE_TYPE);
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
            String estimateId = createDraftEstimate();
            MainSalesDocumentsPage mainSalesDocumentsPage = loginAndGoTo(
                    LoginType.USER_WITH_NEW_INTERFACE_LIKE_35_SHOP, MainSalesDocumentsPage.class);
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
            String estimateId = createDraftEstimate();
            MainSalesDocumentsPage mainSalesDocumentsPage = loginAndGoTo(
                    LoginType.USER_WITH_NEW_INTERFACE_LIKE_35_SHOP, MainSalesDocumentsPage.class);
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
        // TODO Need to API
        // Pre-condition
        MainSalesDocumentsPage mainSalesDocumentsPage = loginAndGoTo(
                LoginType.USER_WITH_NEW_INTERFACE_LIKE_35_SHOP, MainSalesDocumentsPage.class);
        SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
        salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                SalesDocumentsConst.ESTIMATE_TYPE);
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

    // Корзина

    @Test(description = "C22797089 Создать корзину с экрана Документы продажи")
    public void testCreateBasketFromSalesDocumentsScreen() throws Exception {
        // Test data
        String shopId = "35";
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions(shopId, false);
        // Pre-condition
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(
                LoginType.USER_WITH_NEW_INTERFACE_LIKE_35_SHOP, MainProductAndServicesPage.class);
        MainSalesDocumentsPage mainSalesDocumentsPage = setShopAndDepartmentForUser(mainProductAndServicesPage, shopId, "01")
                .goToSales()
                .goToSalesDocumentsSection();

        // Step 1
        log.step("Нажать кнопку Оформить продажу");
        SaleTypeModalPage modalPage = mainSalesDocumentsPage.clickCreateSalesDocumentButton();
        modalPage.verifyRequiredElementsWhenFromSalesDocuments();

        // Step 2
        log.step("Выбрать параметр Корзина");
        Basket35Page basket35Page = modalPage.clickBasketMenuItem();
        basket35Page.verifyRequiredElements(
                new Basket35Page.PageState().setProductIsAdded(false));

        // Step 3
        log.step("Нажмите на кнопку +товары и услуги");
        SearchProductPage searchProductPage = basket35Page.clickAddProductButton()
                .verifyRequiredElements();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page addProduct35Page = new AddProduct35Page(context);
        addProduct35Page.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);

        // Step 4
        log.step("Нажмите на Добавить в корзину");
        basket35Page = addProduct35Page.clickAddIntoBasketButton();
        basket35Page.verifyRequiredElements(new Basket35Page.PageState().setProductIsAdded(true));
    }

    @Test(description = "C22797090 Добавить новый товар в корзину")
    public void testAddNewProductIntoBasket() throws Exception {
        // Test data
        String lmCode = getAnyLmCodeProductWithTopEM();
        // Если выполняется после "C22797089 Создать корзину с экрана Документы продажи",
        // то можно пропустить pre-condition шаги
        if (!Basket35Page.isThisPage(context)) {
            MainSalesDocumentsPage mainSalesDocumentsPage = loginAndGoTo(
                    LoginType.USER_WITH_NEW_INTERFACE_LIKE_35_SHOP, MainSalesDocumentsPage.class);
            SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                    SalesDocumentsConst.BASKET_TYPE);
        } // TODO через API
        Basket35Page basket35Page = new Basket35Page(context);
        int productCountInBasket = basket35Page.getCountOfOrderCards();
        // Step 1
        log.step("Нажмите на кнопку +Товар");
        SearchProductPage searchProductPage = basket35Page.clickAddProductButton()
                .verifyRequiredElements();

        // Step 2
        log.step("Введите ЛМ код товара или название товара или отсканируйте товар");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page addProduct35Page = new AddProduct35Page(context)
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);
        SalesOrderCardData expectedOrderCardData = addProduct35Page.getOrderRowDataFromPage();

        // Step 3
        log.step("Нажмите на Добавить в корзину");
        basket35Page = addProduct35Page.clickAddIntoBasketButton();
        basket35Page.verifyRequiredElements(
                new Basket35Page.PageState()
                        .setProductIsAdded(true)
                        .setManyOrders(null));
        basket35Page.shouldCountOfCardsIs(productCountInBasket + 1);
        basket35Page.shouldOrderCardDataWithTextIs(expectedOrderCardData.getProductCardData().getLmCode(),
                expectedOrderCardData);
    }

    @Test(description = "C22797098 Удалить товар из корзины")
    public void testRemoveProductFromCart() throws Exception {
        if (!Basket35Page.isThisPage(context)) {
            String cartDocId = createDraftCart(2);
            MainSalesDocumentsPage mainSalesDocumentsPage = loginAndGoTo(
                    LoginType.USER_WITH_NEW_INTERFACE_LIKE_35_SHOP, MainSalesDocumentsPage.class);
            SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                    cartDocId);
        }

        Basket35Page basket35Page = new Basket35Page(context);
        SalesOrderCardData salesOrderCardDataBefore = basket35Page.getSalesOrderCardDataByIndex(1);

        // Step 1
        log.step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        CartActionWithProductCardModalPage modalPage = basket35Page.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        log.step("Выберите параметр Удалить товар");
        ConfirmRemovingProductModal confirmRemovingProductModal = modalPage.clickRemoveProductMenuItem()
                .verifyRequiredElements();

        // Step 3
        log.step("Нажмите на Удалить");
        confirmRemovingProductModal.clickConfirmButton();
        basket35Page = new Basket35Page(context);
        basket35Page.verifyRequiredElements(new Basket35Page.PageState()
                .setProductIsAdded(true)
                .setManyOrders(false));
        basket35Page.shouldProductBeNotPresentInCart(
                salesOrderCardDataBefore.getProductCardData().getLmCode());
    }

    // ---------------------- TYPICAL TESTS FOR THIS CLASS -------------------//

    /**
     * Step 1 - Нажмите в поле поиска
     * Step 2 - Введите ЛМ код товара (напр., @param lmcode)
     * Step 3 - Нажмите на кнопку Действия с товаром
     */
    private <T extends CommonActionWithProductModalPage> T testSearchForProductAndClickActionsWithProductButton(
            MainProductAndServicesPage mainProductAndServicesPage, ProductCardData productData, ProductTypes productType, boolean is35Shop) throws Exception {
        // Pre-condition
        if (mainProductAndServicesPage == null)
            mainProductAndServicesPage = loginAndGoTo(LoginType.USER_WITH_OLD_INTERFACE, MainProductAndServicesPage.class);

        // Step #1
        log.step("Нажмите в поле поиска");
        SearchProductPage searchPage = mainProductAndServicesPage.clickSearchBar(false);
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
            MainProductAndServicesPage mainProductAndServicesPage, ProductCardData productData, ProductTypes productType) throws Exception {
        return testSearchForProductAndClickActionsWithProductButton(mainProductAndServicesPage, productData, productType, false);
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
            String testPinCode = SalesDocDataGenerator.getAvailablePinCode(apiClient);
            basketStep3Page.enterPinCode(testPinCode)
                    .shouldPinCodeFieldIs(testPinCode)
                    .shouldSubmitButtonIsActive();

            // Step #9
            log.step("Нажмите кнопку Подтвердить");
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