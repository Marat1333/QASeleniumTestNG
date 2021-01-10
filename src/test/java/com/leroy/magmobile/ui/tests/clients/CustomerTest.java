package com.leroy.magmobile.ui.tests.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.CustomerHelper;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.Gender;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.Smoke;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.OrderClient;
import com.leroy.magmobile.api.clients.SalesDocSearchClient;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.constants.TestDataConstants;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.models.customer.MagLegalCustomerData;
import com.leroy.magmobile.ui.models.sales.OrderDetailsData;
import com.leroy.magmobile.ui.models.sales.ShortSalesDocumentData;
import com.leroy.magmobile.ui.pages.customers.*;
import com.leroy.magmobile.ui.pages.customers.data.PhoneUiData;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SubmittedSalesDocument35Page;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartSearchPage;
import com.leroy.magmobile.ui.pages.sales.orders.estimate.EstimatePage;
import com.leroy.magmobile.ui.pages.sales.orders.estimate.EstimateSearchPage;
import com.leroy.magmobile.ui.pages.sales.orders.estimate.EstimateSubmittedPage;
import com.leroy.magmobile.ui.pages.sales.orders.order.OrderSearchPage;
import com.leroy.magmobile.ui.pages.sales.orders.order.ProcessOrder35Page;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.utils.RandomUtil;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerTest extends AppBaseSteps {

    @Inject
    private CustomerHelper customerHelper;
    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private OrderClient orderClient;
    @Inject
    private SalesDocSearchClient salesDocSearchClient;

    private void cancelOrder(String orderId) throws Exception {
        orderClient.waitUntilOrderCanBeCancelled(orderId);
        Response<JsonNode> r = orderClient.cancelOrder(orderId);
        anAssert().isTrue(r.isSuccessful(),
                "Не смогли удалить заказ №" + orderId + ". Ошибка: " + r.toString());
        orderClient.waitUntilOrderHasStatusAndReturnOrderData(orderId,
                SalesDocumentsConst.States.CANCELLED.getApiVal());
    }

    @Smoke
    @Test(description = "C3201018 Создание клиента (физ. лицо)")
    public void testCreateCustomer() throws Exception {
        MainCustomerPage mainCustomerPage = loginAndGoTo(MainCustomerPage.class);

        // Step 1
        step("Нажмите на кнопку Создать нового клиента");
        NewCustomerInfoPage newCustomerInfoPage = mainCustomerPage.clickCreateNewCustomer()
                .verifyRequiredElements(false);

        // Steps 2-4
        step("Введите имя нового клиента");
        String customerFirstName = RandomUtil.randomCyrillicCharacters(7);
        newCustomerInfoPage.editCustomerFirstName(customerFirstName, true);

        // Step 5
        step("Выберите пол клиента");
        Gender gender = new Random().nextBoolean() ? Gender.MALE : Gender.FEMALE;
        newCustomerInfoPage.selectGender(gender, true);

        // Steps 6-9
        step("Ведите новый номер телефона");
        String phone = RandomUtil.randomPhoneNumber();
        newCustomerInfoPage.editPhoneNumber(1, new PhoneUiData(phone), true);

        // Step 10
        step("Нажмите на Показать все поля");
        newCustomerInfoPage.clickShowAllFieldsButton()
                .verifyRequiredElements(true);

        // Step 11
        step("Нажмите на Скрыть дополнительные поля");
        newCustomerInfoPage.clickHideAdditionalFieldsButton()
                .verifyRequiredElements(false);

        // Step 12
        step("Нажмите на Создать");
        SuccessCustomerPage successCustomerPage = newCustomerInfoPage.clickSubmitButton();
        successCustomerPage.verifyRequiredElements();

        // Step 13
        step("Нажмите на Перейти к списку клиентов");
        MagCustomerData customerData = new MagCustomerData();
        customerData.setPhone(phone);
        customerData.setName(customerFirstName);
        mainCustomerPage = successCustomerPage.clickGoToCustomerListButton();
        mainCustomerPage.shouldRecentCustomerIs(1, customerData);
    }

    /*@Test(description = "C22782860 Просмотр документов клиента (юр.лицо)")
    public void testViewLegalClientInformation() throws Exception {
        step("Выполнение preconditions");
        MagLegalCustomerData customerData = TestDataConstants.LEGAL_ENTITY_1;
        String customerNumber = customerHelper.getFirstCustomerIdByPhone(customerData.getPhone());
        SalesDocSearchClient salesDocSearchClient = apiClientProvider.getSalesDocSearchClient();

        // Корзины
        Response<SalesDocumentListResponse> respCart = salesDocSearchClient.searchForDocuments(
                new SalesDocSearchClient.Filters().setCustomerNumber(customerNumber)
                        .setDocType(SalesDocumentsConst.Types.CART.getApiVal()));
        anAssert().isTrue(respCart.isSuccessful(), respCart.toString());
        SalesDocumentListResponse responseCartBody = respCart.asJson();
        List<ShortSalesDocumentData> expectedCarts = convertToShortSalesDocumentData(responseCartBody.getSalesDocuments());

        // Документы продажи
        Response<SalesDocumentListResponse> respSalesDoc = salesDocSearchClient.searchForDocuments(
                new SalesDocSearchClient.Filters().setCustomerNumber(customerNumber)
                        .setDocType(SalesDocumentsConst.Types.ORDER.getApiVal()));
        anAssert().isTrue(respCart.isSuccessful(), respCart.toString());
        SalesDocumentListResponse responseSalesDocBody = respSalesDoc.asJson();
        List<ShortSalesDocumentData> expectedSalesDoc = convertToShortSalesDocumentData(
                responseSalesDocBody.getSalesDocuments());

        // Сметы
        Response<SalesDocumentListResponse> respEstimate = salesDocSearchClient.searchForDocuments(
                new SalesDocSearchClient.Filters().setCustomerNumber(customerNumber)
                        .setDocType(SalesDocumentsConst.Types.ESTIMATE.getApiVal()));
        anAssert().isTrue(respCart.isSuccessful(), respCart.toString());
        SalesDocumentListResponse responseEstimatesBody = respEstimate.asJson();
        List<ShortSalesDocumentData> expectedEstimatesDoc = convertToShortSalesDocumentData(
                responseEstimatesBody.getSalesDocuments());

        MainCustomerPage mainCustomerPage = loginAndGoTo(MainCustomerPage.class);
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField()
                .verifyRequiredElements();
        searchCustomerPage.searchCustomerByPhone(customerData.getPhone());

        ViewCustomerPage viewCustomerPage = new ViewCustomerPage()
                .verifyRequiredElements();

        // Step 1
        step("Нажмите на Корзины");
        CartSearchPage cartSearchPage = viewCustomerPage.goToCarts();
        cartSearchPage.verifyRequiredElements();
        cartSearchPage.shouldFirstDocumentsAre(expectedCarts);

        // Step 2
        step("Нажмите на стрелку назад");
        viewCustomerPage = cartSearchPage.clickBackButton();
        viewCustomerPage.shouldCartsCountIs(responseCartBody.getTotalCount());

        // Step 3
        step("Нажмите на Документы продажи");
        OrderSearchPage orderSearchPage = viewCustomerPage.goToSalesDocuments();
        orderSearchPage.verifyRequiredElements();
        orderSearchPage.shouldFirstDocumentsAre(expectedSalesDoc);

        // Step 4
        step("Нажмите на стрелку назад");
        viewCustomerPage = orderSearchPage.clickBackButton();
        viewCustomerPage.shouldSalesDocCountIs(responseSalesDocBody.getTotalCount());

        // Step 5
        step("Нажмите на Сметы");
        EstimateSearchPage estimateSearchPage = viewCustomerPage.goToEstimates();
        estimateSearchPage.verifyRequiredElements();
        estimateSearchPage.shouldFirstDocumentsAre(expectedEstimatesDoc);

        // Step 6
        step("Нажмите на стрелку назад");
        viewCustomerPage = estimateSearchPage.clickBackButton();
        viewCustomerPage.shouldEstimatesCountIs(responseEstimatesBody.getTotalCount());
    }*/

    @Smoke
    @Test(description = "C3201020 Поиск клиента по телефону (физ. лицо)")
    public void testSearchForIndividualCustomerByPhone() throws Exception {
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;

        MainCustomerPage mainCustomerPage = loginAndGoTo(MainCustomerPage.class);

        // Step 1
        step("Нажмите на поле Поиск клиента");
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField()
                .verifyRequiredElements();

        // Step 2
        step("Введите номер клиента");
        searchCustomerPage.searchCustomerByPhone(customerData.getPhone(), false);
        searchCustomerPage.shouldFirstCustomerIs(customerData, SearchCustomerPage.SearchType.BY_PHONE);
    }

    @Test(description = "C3201021 Поиск клиента по email (физ. лицо)")
    public void testSearchForClientByEmail() throws Exception {
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;

        MainCustomerPage mainCustomerPage = loginAndGoTo(MainCustomerPage.class);

        // Step 1
        step("Нажмите на поле Поиск клиента");
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField()
                .verifyRequiredElements();

        // Step 2
        step("Введите электронную почту клиента");
        searchCustomerPage.searchCustomerByEmail(customerData.getEmail(), false);
        searchCustomerPage.shouldFirstCustomerIs(customerData, SearchCustomerPage.SearchType.BY_EMAIL);
    }

    @Test(description = "C22907529 Поиск клиента (юр. лицо) по номеру договора")
    public void testSearchForLegalClientByContractNumber() throws Exception {
        MagLegalCustomerData customerData = TestDataConstants.LEGAL_ENTITY_1;

        MainCustomerPage mainCustomerPage = loginAndGoTo(MainCustomerPage.class);

        // Step 1
        step("Нажмите на поле Поиск клиента");
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField()
                .verifyRequiredElements();

        // Step 2
        step("Введите электронную почту клиента");
        searchCustomerPage.searchLegalCustomerByContractNumber(customerData.getContractNumber(), false);
        searchCustomerPage.shouldFirstCustomerIs(customerData);
    }

    @Test(description = "C3201063 Проверка списка недавно просмотренных клиентов")
    public void testListOfRecentlyViewedClients() throws Exception {
        MagCustomerData customerData1 = TestDataConstants.CUSTOMER_DATA_1;
        MagCustomerData customerData2 = TestDataConstants.CUSTOMER_DATA_2;

        MainCustomerPage mainCustomerPage = loginAndGoTo(MainCustomerPage.class);
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField();
        searchCustomerPage.searchCustomerByPhone(customerData1.getPhone(), true);
        ViewCustomerPage viewCustomerPage = new ViewCustomerPage();
        viewCustomerPage.clickBackButton();
        searchCustomerPage = new SearchCustomerPage();
        searchCustomerPage.searchCustomerByPhone(customerData2.getPhone(), true);
        viewCustomerPage = new ViewCustomerPage();
        viewCustomerPage.clickBackButton();
        searchCustomerPage = new SearchCustomerPage();
        searchCustomerPage.clickBackButton();

        // Step 1
        step("Нажмите на мини-карточку последнего клиента из списка Недавние клиенты");
        mainCustomerPage = new MainCustomerPage();
        mainCustomerPage.shouldRecentCustomerIs(1, customerData2)
                .shouldRecentCustomerIs(2, customerData1);
        viewCustomerPage = mainCustomerPage.clickRecentClient(2).
                shouldCurrentClientIs(customerData1);

        // Step 2
        step("Нажмите на стрелку назад");
        viewCustomerPage.clickBackButton();
        mainCustomerPage = new MainCustomerPage();
        mainCustomerPage.shouldRecentCustomerIs(1, customerData1)
                .shouldRecentCustomerIs(2, customerData2);
    }

    @Smoke
    @Test(description = "C22782859 Просмотр документов клиента (физ.лицо)")
    public void testViewIndividualClientInformation() throws Exception {
        step("Выполнение preconditions");
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;
        String customerNumber = customerHelper.getFirstCustomerIdByPhone(customerData.getPhone());

        // Корзины
        Response<SalesDocumentListResponse> respCart = salesDocSearchClient.searchForDocuments(
                new SalesDocSearchClient.Filters().setCustomerNumber(customerNumber)
                        .setDocType(SalesDocumentsConst.Types.CART.getApiVal()));
        anAssert().isTrue(respCart.isSuccessful(), respCart.toString());
        SalesDocumentListResponse responseCartBody = respCart.asJson();
        List<ShortSalesDocumentData> expectedCarts = convertToShortSalesDocumentData(responseCartBody.getSalesDocuments());

        // Документы продажи
        Response<SalesDocumentListResponse> respSalesDoc = salesDocSearchClient.searchForDocuments(
                new SalesDocSearchClient.Filters().setCustomerNumber(customerNumber)
                        .setDocType(SalesDocumentsConst.Types.ORDER.getApiVal()));
        anAssert().isTrue(respCart.isSuccessful(), respCart.toString());
        SalesDocumentListResponse responseSalesDocBody = respSalesDoc.asJson();
        List<ShortSalesDocumentData> expectedSalesDoc = convertToShortSalesDocumentData(
                responseSalesDocBody.getSalesDocuments());

        // Сметы
        Response<SalesDocumentListResponse> respEstimate = salesDocSearchClient.searchForDocuments(
                new SalesDocSearchClient.Filters().setCustomerNumber(customerNumber)
                        .setDocType(SalesDocumentsConst.Types.ESTIMATE.getApiVal()));
        anAssert().isTrue(respCart.isSuccessful(), respCart.toString());
        SalesDocumentListResponse responseEstimatesBody = respEstimate.asJson();
        List<ShortSalesDocumentData> expectedEstimatesDoc = convertToShortSalesDocumentData(
                responseEstimatesBody.getSalesDocuments());

        MainCustomerPage mainCustomerPage = loginAndGoTo(MainCustomerPage.class);
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField()
                .verifyRequiredElements();
        searchCustomerPage.searchCustomerByPhone(customerData.getPhone());

        ViewCustomerPage viewCustomerPage = new ViewCustomerPage()
                .verifyRequiredElements();

        // Step 1
        step("Нажмите на Корзины");
        CartSearchPage cartSearchPage = viewCustomerPage.goToCarts();
        cartSearchPage.verifyRequiredElements();
        cartSearchPage.shouldFirstDocumentsAre(expectedCarts);

        // Step 2
        step("Нажмите на стрелку назад");
        viewCustomerPage = cartSearchPage.clickBackButton();
        viewCustomerPage.shouldCartsCountIs(responseCartBody.getTotalCount());

        // Step 3
        step("Нажмите на Документы продажи");
        OrderSearchPage orderSearchPage = viewCustomerPage.goToSalesDocuments();
        orderSearchPage.verifyRequiredElements();
        orderSearchPage.shouldFirstDocumentsAre(expectedSalesDoc);

        // Step 4
        step("Нажмите на стрелку назад");
        viewCustomerPage = orderSearchPage.clickBackButton();
        viewCustomerPage.shouldSalesDocCountIs(responseSalesDocBody.getTotalCount());

        // Step 5
        step("Нажмите на Сметы");
        EstimateSearchPage estimateSearchPage = viewCustomerPage.goToEstimates();
        estimateSearchPage.verifyRequiredElements();
        estimateSearchPage.shouldFirstDocumentsAre(expectedEstimatesDoc);

        // Step 6
        step("Нажмите на стрелку назад");
        viewCustomerPage = estimateSearchPage.clickBackButton();
        viewCustomerPage.shouldEstimatesCountIs(responseEstimatesBody.getTotalCount());
    }

    @Test(description = "C22797105 Создание сметы с карточки клиента")
    public void testCreateEstimateFromClientPage() throws Exception {
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;
        String lmCode = searchProductHelper.getProducts(1, false, false).get(0).getLmCode();

        // Pre-condition
        MainCustomerPage mainCustomerPage = loginSelectShopAndGoTo(MainCustomerPage.class);
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField();
        searchCustomerPage.searchCustomerByPhone(customerData.getPhone(), true);

        // Step 1
        step("Нажмите на Сметы");
        EstimateSearchPage estimateSearchPage = new ViewCustomerPage().goToEstimates();

        // Step 2
        step("Нажмите на Создать смету");
        EstimatePage estimatePage = estimateSearchPage.clickCreateEstimateButton();

        // Step 3
        step("Нажмите на кнопку +Товары и услуги");
        SearchProductPage searchProductPage = estimatePage.clickProductAndServiceButton();
        searchProductPage.verifyRequiredElements();

        // Step 4
        step("Введите ЛМ код товара или название товара или отсканируйте товар");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page<EstimatePage> addProduct35Page = new AddProduct35Page<>(EstimatePage.class);
        addProduct35Page.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_ESTIMATE);

        // Step 5
        step("Нажмите на Добавить в смету");
        estimatePage = addProduct35Page.clickAddIntoEstimateButton();
        EstimatePage.PageState pageState = EstimatePage.PageState.builder()
                .customerIsSelected(true).productIsAdded(true).build();
        estimatePage.verifyRequiredElements(pageState);

        // Step 6
        step("Нажмите на Создать");
        Double expectedTotalPrice = estimatePage.getTotalPrice();
        String documentNumber = estimatePage.getDocumentNumber(true);
        EstimateSubmittedPage estimateSubmittedPage = estimatePage.clickCreateButton()
                .verifyRequiredElements();

        // Step 7
        step("Нажать на Перейти в список документов");
        ShortSalesDocumentData expectedSalesDocument = new ShortSalesDocumentData();
        expectedSalesDocument.setDocumentTotalPrice(expectedTotalPrice);
        expectedSalesDocument.setDocumentState(SalesDocumentsConst.States.CONFIRMED.getUiVal());
        expectedSalesDocument.setTitle(SalesDocumentsConst.Types.ESTIMATE.getUiVal());
        expectedSalesDocument.setNumber(documentNumber);
        SalesDocumentsPage salesDocumentsPage = estimateSubmittedPage.clickSubmitButton();
        salesDocumentsPage.verifyRequiredElements()
                .shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);
    }

    @Test(description = "C22797106 Создание корзины с карточки клиента")
    public void testCreateCartFromClientPage() throws Exception {
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;
        String lmCode = searchProductHelper.getProducts(1, false, false).get(0).getLmCode();

        // Pre-condition
        MainCustomerPage mainCustomerPage = loginSelectShopAndGoTo(MainCustomerPage.class);
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField();
        searchCustomerPage.searchCustomerByPhone(customerData.getPhone(), true);

        // Step 1
        step("Нажмите на Корзины");
        CartSearchPage cartSearchPage = new ViewCustomerPage().goToCarts();

        // Step 2
        step("Нажмите на Создать корзину");
        Cart35Page cart35Page = cartSearchPage.clickCreateCartButton();

        // Step 3
        step("Нажмите на кнопку +Товары и услуги");
        SearchProductPage searchProductPage = cart35Page.clickAddProductButton()
                .verifyRequiredElements();

        // Step 4
        step("Введите ЛМ код товара");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page<Cart35Page> addProduct35Page = new AddProduct35Page<>(Cart35Page.class);
        addProduct35Page.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);

        // Step 5
        step("Нажмите на Добавить в корзину");
        cart35Page = addProduct35Page.clickAddIntoBasketButton();
        cart35Page.verifyRequiredElements(Cart35Page.PageState.builder()
                .productIsAdded(true)
                .build());

        // TODO
        // Шаги 6 и 7 пропущены пока что, так как номер корзины на странице корзины не отображается, а
        // по каким другим критериям можно найти именно созданную корзину в списке - не понятно.

    }

    @Test(description = "C22797108 Создание заказа из корзины, созданной из карточки клиента")
    public void testCreateOrderFromClientPage() throws Exception {
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;

        // Pre-condition
        MainCustomerPage mainCustomerPage = loginSelectShopAndGoTo(MainCustomerPage.class);
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField();
        searchCustomerPage.searchCustomerByPhone(customerData.getPhone(), true);

        // Step 1
        step("Нажмите на Корзины");
        CartSearchPage cartSearchPage = new ViewCustomerPage().goToCarts();

        // Step 2
        step("Нажмите на мини-карточку нужной корзины");
        cartSearchPage.clickFirstSalesDoc();
        Cart35Page cart35Page = new Cart35Page();
        double totalPrice = cart35Page.getTotalPrice();

        // Step 3
        step("Нажмите на кнопку Оформить");
        ProcessOrder35Page processOrder35Page = cart35Page.clickMakeSalesButton();

        // Step 4-6
        step("Введите имя и фамилию нового пользователя, Введите PIN-код для оплаты");
        OrderDetailsData orderDetailsData = new OrderDetailsData().setRequiredRandomData();
        orderDetailsData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
        processOrder35Page.fillInFormFields(orderDetailsData)
                .shouldFormFieldsAre(orderDetailsData);

        // Step 7
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
        try {
            cancelOrder(documentNumber);
        } catch (AssertionError | Exception err) {
            Log.error(err.getMessage());
        }
    }

    // ----------- Private methods ------------ //

    /**
     * Convert List<SalesDocumentResponseData> to List<ShortSalesDocumentData>
     */
    private List<ShortSalesDocumentData> convertToShortSalesDocumentData(
            List<SalesDocumentResponseData> apiResponseData) {
        if (apiResponseData.size() > 6)
            apiResponseData = apiResponseData.subList(0, 6);
        List<ShortSalesDocumentData> expectedDocData = new ArrayList<>();
        for (SalesDocumentResponseData salesDocumentResponseData : apiResponseData) {
            ShortSalesDocumentData shortSalesDocumentData = new ShortSalesDocumentData();
            if (salesDocumentResponseData.getDocType().equals(SalesDocumentsConst.Types.CART.getApiVal()))
                shortSalesDocumentData.setTitle(SalesDocumentsConst.Types.CART.getUiVal());
            else if (salesDocumentResponseData.getDocType().equals(SalesDocumentsConst.Types.ESTIMATE.getApiVal()))
                shortSalesDocumentData.setTitle(SalesDocumentsConst.Types.ESTIMATE.getUiVal());
            else if (salesDocumentResponseData.getDocType().equals(SalesDocumentsConst.Types.ORDER.getApiVal()))
                shortSalesDocumentData.setTitle(salesDocumentResponseData.getGiveAway().getPoint()
                        .equals(SalesDocumentsConst.GiveAwayPoints.PICKUP.getApiVal()) ?
                        SalesDocumentsConst.GiveAwayPoints.PICKUP.getUiVal() : SalesDocumentsConst.GiveAwayPoints.DELIVERY.getUiVal());
            else
                shortSalesDocumentData.setTitle("UNKNOWN");
            shortSalesDocumentData.setDocumentTotalPrice(salesDocumentResponseData.getDocPriceSum());
            shortSalesDocumentData.setNumber(salesDocumentResponseData.getFullDocId());
            expectedDocData.add(shortSalesDocumentData);
        }
        return expectedDocData;
    }

}
