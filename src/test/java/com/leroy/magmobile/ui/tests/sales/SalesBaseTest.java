package com.leroy.magmobile.ui.tests.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.api.Module;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.ApiClientProvider;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SubmittedSalesDocumentPage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketPage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep1Page;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep2Page;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep3Page;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.umbrella_extension.authorization.AuthClient;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

@Guice(modules = {Module.class})
public class SalesBaseTest extends AppBaseSteps {

    @Inject
    private AuthClient authClient;

    @Inject
    protected ApiClientProvider clientProvider;

    @BeforeClass
    public void salesBaseTestBeforeClass() {
        String token = authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS);
        sessionData.setAccessToken(token);
        clientProvider.setSessionData(sessionData);
    }

    // Получить ЛМ код для услуги
    protected String getAnyLmCodeOfService() {
        return EnvConstants.SERVICE_1_LM_CODE;
    }

    // Получить ЛМ код для обычного продукта без специфичных опций
    protected List<String> getAnyLmCodesProductWithoutSpecificOptions(
            int necessaryCount) {
        return clientProvider.getProducts(necessaryCount, false, false)
                .stream().map(ProductItemData::getLmCode).collect(Collectors.toList());
    }

    protected String getAnyLmCodeProductWithoutSpecificOptions() {
        return getAnyLmCodesProductWithoutSpecificOptions(1).get(0);
    }

    // Получить ЛМ код для продукта с AVS
    protected String getAnyLmCodeProductWithAvs() {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setAvs(true);
        return clientProvider.getProducts(1, filtersData).get(0).getLmCode();
    }

    // Получить ЛМ код для продукта с опцией TopEM
    protected String getAnyLmCodeProductWithTopEM() {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setTopEM(true);
        context.getSessionData().setUserDepartmentId("15");
        return clientProvider.getProducts(1, filtersData).get(0).getLmCode();
    }

    // Получить ЛМ код для продукта, доступного для отзыва с RM
    protected String getAnyLmCodeProductIsAvailableForWithdrawalFromRM() {
        return "18845896";
    }

    protected String getValidPinCode() {
        int tryCount = 10;
        for (int i = 0; i < tryCount; i++) {
            String generatedPinCode;
            do {
                generatedPinCode = RandomStringUtils.randomNumeric(5);
            } while (generatedPinCode.startsWith("9"));
            SalesDocumentListResponse salesDocumentsResponse = clientProvider.getCatalogSearchClient().getSalesDocumentsByPinCodeOrDocId(generatedPinCode)
                    .asJson();
            if (salesDocumentsResponse.getTotalCount() == 0) {
                Log.info("API: Не найдено ни одного документа с PIN кодом: " + generatedPinCode);
                return generatedPinCode;
            }
            List<SalesDocumentResponseData> salesDocs = salesDocumentsResponse.getSalesDocuments();
            if (!generatedPinCode.equals(salesDocs.get(0).getPinCode())) {
                return generatedPinCode;
            }
        }
        throw new RuntimeException("Мы не смогли за " + tryCount + " попыток подобрать неиспользованный PIN код");
    }

    // CREATING PRE-CONDITIONS:

    /*protected String createDraftEstimate() {
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions();
        CartEstimateProductOrderData productOrderData = new CartEstimateProductOrderData();
        productOrderData.setLmCode(lmCode);
        productOrderData.setQuantity(1.0);
        Response<EstimateData> estimateDataResponse = estimateClient.sendRequestCreate(productOrderData);
        assertThat(estimateDataResponse, successful());
        return estimateDataResponse.asJson().getEstimateId();
    }*/

    protected String createDraftCart(int productCount) {
        List<String> lmCodes = getAnyLmCodesProductWithoutSpecificOptions(productCount);
        List<CartProductOrderData> productOrderDataList = new ArrayList<>();
        Random r = new Random();
        for (String lmCode : lmCodes) {
            CartProductOrderData productOrderData = new CartProductOrderData();
            productOrderData.setLmCode(lmCode);
            productOrderData.setQuantity((double) (r.nextInt(9) + 1));
            productOrderDataList.add(productOrderData);
        }
        Response<CartData> cartDataResponse = clientProvider.getCartClient().sendRequestCreate(productOrderDataList);
        assertThat(cartDataResponse, successful());
        return cartDataResponse.asJson().getFullDocId();
    }

    protected void cancelOrder(String orderId) throws Exception {
        Response<JsonNode> r = clientProvider.getOrderClient().cancelOrder(orderId);
        if (!r.isSuccessful()) {
            Thread.sleep(10000); // TODO можно подумать над не implicit wait'ом
            Log.warn(r.toString());
            r = clientProvider.getOrderClient().cancelOrder(orderId);
        }
        anAssert.isTrue(r.isSuccessful(),
                "Не смогли удалить заказ №" + orderId + ". Ошибка: " + r.toString());
    }

    // Product Types
    protected enum ProductTypes {
        NORMAL, AVS, TOP_EM;
    }

    // TESTS

    @Test(description = "C3201029 Создание документа продажи")
    public void testCreateDocumentSales() throws Exception {
        sessionData.setUserShopId(EnvConstants.SHOP_WITH_OLD_INTERFACE);
        //sessionData.setUserDepartmentId("15");
        // Step #1
        step("На главном экране выберите раздел Документы продажи");
        MainSalesDocumentsPage salesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);
        salesDocumentsPage.verifyRequiredElements();

        // Step #2
        step("Нажмите 'Создать документ продажи'");
        SearchProductPage searchProductPage = salesDocumentsPage.clickCreateSalesDocumentButton();
        searchProductPage.verifyRequiredElements();

        // Step #3
        step("Нажмите на мини-карточку товара 16410291");
        searchProductPage.searchProductAndSelect("16410291");
        AddProductPage addProductPage = new AddProductPage(context)
                .verifyRequiredElements();

        // Step #4
        step("Нажмите на поле количества");
        addProductPage.clickEditQuantityField()
                .shouldKeyboardVisible();
        addProductPage.shouldEditQuantityFieldIs("1,00")
                .shouldTotalPriceIs(String.format("%.2f", Double.parseDouble(
                        addProductPage.getPrice()))); // TODO Jenkins job failed here

        // Step #5
        step("Введите значение 20,5 количества товара");
        String expectedTotalPrice = String.format("%.2f",
                Double.parseDouble(addProductPage.getPrice()) * 20.5);
        addProductPage.enterQuantityOfProduct("20,5")
                .shouldTotalPriceIs(expectedTotalPrice);

        // Step #6
        step("Нажмите кнопку Добавить");
        BasketStep1Page basketStep1Page = addProductPage.clickAddButton()
                .verifyRequiredElements();
        basketStep1Page.shouldDocumentTypeIs(BasketPage.Constants.DRAFT_DOCUMENT_TYPE);
        String documentNumber = basketStep1Page.getDocumentNumber();

        // Step #7
        step("Нажмите Далее к параметрам");
        BasketStep2Page basketStep2Page = basketStep1Page.clickNextParametersButton()
                .verifyRequiredElements()
                .shouldFieldsHaveDefaultValues();

        // Step #8
        step("Нажмите кнопку Создать документ продажи");
        BasketStep3Page basketStep3Page = basketStep2Page.clickCreateSalesDocumentButton()
                .verifyRequiredElements();
        basketStep3Page.shouldKeyboardVisible();

        // Step #9
        step("Введите 5 цифр PIN-кода");
        String testPinCode = getValidPinCode();
        basketStep3Page.enterPinCode(testPinCode)
                .shouldPinCodeFieldIs(testPinCode)
                .shouldSubmitButtonIsActive();

        // Step #10
        step("Нажмите кнопку Подтвердить");
        SubmittedSalesDocumentPage submittedSalesDocumentPage = basketStep3Page.clickSubmitButton()
                .verifyRequiredElements()
                .shouldPinCodeIs(testPinCode)
                .shouldDocumentNumberIs(documentNumber);

        // Step #11
        step("Нажмите кнопку Перейти в список документов");
        SalesDocumentData expectedSalesDocument = new SalesDocumentData();
        expectedSalesDocument.setPrice(expectedTotalPrice);
        expectedSalesDocument.setPin(testPinCode);
        expectedSalesDocument.setDocumentState(SalesDocumentsConst.States.CONFIRMED.getUiVal());
        expectedSalesDocument.setTitle("Из торгового зала");
        expectedSalesDocument.setNumber(documentNumber);
        submittedSalesDocumentPage.clickSubmitButton()
                .shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);
    }

}
