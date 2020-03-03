package com.leroy.magmobile.ui.tests.sales;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SubmittedSalesDocumentPage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketPage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep1Page;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep2Page;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep3Page;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.CartData;
import com.leroy.umbrella_extension.magmobile.data.ProductItemData;
import com.leroy.umbrella_extension.magmobile.data.estimate.EstimateData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentListResponse;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentResponseData;
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
public class SalesBaseTest extends AppBaseSteps {

    @Inject
    private AuthClient authClient;

    @Inject
    private MagMobileClient mashupClient;

    // Получить ЛМ код для услуги
    protected String getAnyLmCodeOfService() {
        return "49055102";
    }

    // Получить ЛМ код для обычного продукта без специфичных опций
    protected List<String> getAnyLmCodesProductWithoutSpecificOptions(int necessaryCount, String shopId,
                                                                      Boolean hasAvailableStock) {
        String[] badLmCodes = {"10008698", "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        if (shopId == null) // TODO может быть shopId null или нет?
            shopId = "5";
        GetCatalogSearch params = new GetCatalogSearch()
                .setShopId(shopId)
                .setTopEM(false)
                .setHasAvailableStock(hasAvailableStock);
        List<ProductItemData> items = mashupClient.searchProductsBy(params).asJson().getItems();
        List<String> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemData item : items) {
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

    protected String getAnyLmCodeProductWithoutSpecificOptions(String shopId, Boolean hasAvailableStock) {
        return getAnyLmCodesProductWithoutSpecificOptions(1, shopId, hasAvailableStock).get(0);
    }

    // Получить ЛМ код для продукта с AVS
    protected String getAnyLmCodeProductWithAvs() {
        GetCatalogSearch params = new GetCatalogSearch()
                .setTopEM(false);
        List<ProductItemData> items = mashupClient.searchProductsBy(params).asJson().getItems();
        for (ProductItemData item : items) {
            if (item.getAvsDate() != null)
                return item.getLmCode();
        }
        return "82014172";
    }

    // Получить ЛМ код для продукта с опцией TopEM
    protected String getAnyLmCodeProductWithTopEM() {
        GetCatalogSearch params = new GetCatalogSearch()
                .setTopEM(true)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID);
        List<ProductItemData> items = mashupClient.searchProductsBy(params).asJson().getItems();
        for (ProductItemData item : items) {
            if (item.getAvsDate() == null)
                return item.getLmCode();
        }
        if (items.size() > 0)
            return items.get(0).getLmCode();
        return "82138074";
    }

    // Получить ЛМ код для продукта, доступного для отзыва с RM
    protected String getAnyLmCodeProductIsAvailableForWithdrawalFromRM() {
        return "82001470";
    }

    protected String getValidPinCode() {
        int tryCount = 10;
        for (int i = 0; i < tryCount; i++) {
            String generatedPinCode;
            do {
                generatedPinCode = RandomStringUtils.randomNumeric(5);
            } while (generatedPinCode.startsWith("9"));
            SalesDocumentListResponse salesDocumentsResponse = mashupClient.getSalesDocumentsByPinCodeOrDocId(generatedPinCode)
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

    protected String createDraftEstimate() {
        String shopId = "35";
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions(shopId, false);
        String token = authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS);
        ProductOrderData productOrderData = new ProductOrderData();
        productOrderData.setLmCode(lmCode);
        productOrderData.setQuantity(1.0);
        Response<EstimateData> estimateDataResponse = mashupClient
                .createEstimate(token, "35", productOrderData);
        Assert.assertTrue(estimateDataResponse.isSuccessful(),
                "Не смогли создать Смету на этапе создания pre-condition данных");
        return estimateDataResponse.asJson().getEstimateId();
    }

    protected String createDraftCart(int productCount) {
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
        Response<CartData> cartDataResponse = mashupClient
                .createCart(token, "35", productOrderDataList);
        Assert.assertTrue(cartDataResponse.isSuccessful(),
                "Не смогли создать Корзину на этапе создания pre-condition данных");
        return cartDataResponse.asJson().getFullDocId();
    }

    protected void cancelOrder(String orderId) throws Exception {
        Response<JSONObject> r = mashupClient.cancelOrder(EnvConstants.BASIC_USER_LDAP, orderId);
        if (!r.isSuccessful()) {
            Thread.sleep(10000); // TODO можно подумать над не implicit wait'ом
            Log.warn(r.toString());
            r = mashupClient.cancelOrder(EnvConstants.BASIC_USER_LDAP, orderId);
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
        String testPinCode = getValidPinCode();
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
        expectedSalesDocument.setDocumentState(SalesDocumentsConst.States.CREATED.getUiVal());
        expectedSalesDocument.setTitle("Из торгового зала");
        expectedSalesDocument.setNumber(documentNumber);
        submittedSalesDocumentPage.clickSubmitButton()
                .shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);
    }

}
