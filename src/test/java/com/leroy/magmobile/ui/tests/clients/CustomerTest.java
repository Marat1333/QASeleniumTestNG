package com.leroy.magmobile.ui.tests.clients;

import com.google.inject.Inject;
import com.leroy.constants.Gender;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.Smoke;
import com.leroy.magmobile.api.clients.SalesDocSearchClient;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.common_mashups.helpers.CustomerHelper;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.constants.TestDataConstants;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.models.customer.MagLegalCustomerData;
import com.leroy.magmobile.ui.models.sales.ShortSalesDocumentData;
import com.leroy.magmobile.ui.pages.customers.*;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartSearchPage;
import com.leroy.magmobile.ui.pages.sales.orders.estimate.EstimateSearchPage;
import com.leroy.magmobile.ui.pages.sales.orders.order.OrderSearchPage;
import com.leroy.utils.RandomUtil;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerTest extends AppBaseSteps {

    @Inject
    CustomerHelper customerHelper;

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
        newCustomerInfoPage.editPhoneNumber(phone, true);

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

    @Smoke
    @Test(description = "C22782859 Просмотр документов клиента (физ.лицо)")
    public void testViewIndividualClientInformation() throws Exception {
        step("Выполнение preconditions");
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;
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
