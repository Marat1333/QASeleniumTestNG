package com.leroy.magmobile.ui.tests.sales;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.DiscountConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.api.Module;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.OrderClient;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartDiscountData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartDiscountReasonData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.models.sales.ShortSalesDocumentData;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SubmittedSalesDocumentPage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartPage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartStep1Page;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartStep2Page;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartStep3Page;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.leroy.constants.sales.DiscountConst.TYPE_NEW_PRICE;
import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

@Guice(modules = {Module.class})
public class SalesBaseTest extends AppBaseSteps {

    // Получить ЛМ код для услуги
    protected String getAnyLmCodeOfService() {
        return EnvConstants.SERVICE_1_LM_CODE;
    }

    // Получить ЛМ код для обычного продукта без специфичных опций
    protected List<String> getAnyLmCodesProductWithoutSpecificOptions(
            int necessaryCount) {
        return apiClientProvider.getProducts(necessaryCount, false, false)
                .stream().map(ProductItemData::getLmCode).collect(Collectors.toList());
    }

    protected String getAnyLmCodeProductWithoutSpecificOptions() {
        return getAnyLmCodesProductWithoutSpecificOptions(1).get(0);
    }

    // Получить ЛМ код для продукта с AVS
    protected String getAnyLmCodeProductWithAvs() {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setAvs(true);
        return apiClientProvider.getProducts(1, filtersData).get(0).getLmCode();
    }

    // Получить ЛМ код для продукта с опцией TopEM
    protected String getAnyLmCodeProductWithTopEM() {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setTopEM(true);
        filtersData.setAvs(false);
        getUserSessionData().setUserDepartmentId("15");
        return apiClientProvider.getProducts(1, filtersData).get(0).getLmCode();
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
            SalesDocumentListResponse salesDocumentsResponse = apiClientProvider.getSalesDocSearchClient()
                    .getSalesDocumentsByPinCodeOrDocId(generatedPinCode)
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

    protected String createDraftCart(int productCount) {
        return createDraftCart(productCount, false);
    }

    protected String createDraftCart(int productCount, boolean hasDiscount) {
        CartClient cartClient = apiClientProvider.getCartClient();
        List<String> lmCodes = getAnyLmCodesProductWithoutSpecificOptions(productCount);
        List<CartProductOrderData> productOrderDataList = new ArrayList<>();
        Random r = new Random();
        for (String lmCode : lmCodes) {
            CartProductOrderData productOrderData = new CartProductOrderData();
            productOrderData.setLmCode(lmCode);
            productOrderData.setQuantity((double) (r.nextInt(9) + 1));
            productOrderDataList.add(productOrderData);
        }
        Response<CartData> cartDataResponse = cartClient.sendRequestCreate(productOrderDataList);
        assertThat(cartDataResponse, successful());
        CartData cartData = cartDataResponse.asJson();
        if (hasDiscount) {
            productOrderDataList = cartData.getProducts();
            for (CartProductOrderData putProduct : productOrderDataList) {
                CartDiscountData discountData = new CartDiscountData();
                discountData.setType(TYPE_NEW_PRICE);
                discountData.setTypeValue(putProduct.getPrice() - 1);
                discountData.setReason(new CartDiscountReasonData(DiscountConst.Reasons.PRODUCT_SAMPLE.getId()));
                putProduct.setDiscount(discountData);
            }
            Response<CartData> respDiscount = cartClient.addDiscount(
                    cartData.getCartId(), cartData.getDocumentVersion(), productOrderDataList);
            assertThat(respDiscount, successful());
        }
        return cartData.getFullDocId();
    }

    protected void cancelOrder(String orderId) throws Exception {
        OrderClient orderClient = apiClientProvider.getOrderClient();
        Response<JsonNode> r = orderClient.cancelOrder(orderId);
        if (!r.isSuccessful()) {
            Thread.sleep(10000); // TODO можно подумать над не implicit wait'ом
            Log.warn(r.toString());
            r = orderClient.cancelOrder(orderId);
        }
        anAssert().isTrue(r.isSuccessful(),
                "Не смогли удалить заказ №" + orderId + ". Ошибка: " + r.toString());
    }

    // Product Types
    protected enum ProductTypes {
        NORMAL, AVS, TOP_EM;
    }

    // TESTS

    @Test(description = "C3201029 Создание документа продажи", groups = OLD_SHOP_GROUP)
    public void testCreateDocumentSales() throws Exception {
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
        AddProductPage addProductPage = new AddProductPage()
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
        CartStep1Page basketStep1Page = addProductPage.clickAddButton()
                .verifyRequiredElements();
        basketStep1Page.shouldDocumentTypeIs(CartPage.Constants.DRAFT_DOCUMENT_TYPE);
        String documentNumber = basketStep1Page.getDocumentNumber();

        // Step #7
        step("Нажмите Далее к параметрам");
        CartStep2Page basketStep2Page = basketStep1Page.clickNextParametersButton()
                .verifyRequiredElements()
                .shouldFieldsHaveDefaultValues();

        // Step #8
        step("Нажмите кнопку Создать документ продажи");
        CartStep3Page basketStep3Page = basketStep2Page.clickCreateSalesDocumentButton()
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
        ShortSalesDocumentData expectedSalesDocument = new ShortSalesDocumentData();
        expectedSalesDocument.setDocumentTotalPrice(expectedTotalPrice);
        expectedSalesDocument.setPin(testPinCode);
        expectedSalesDocument.setDocumentState(SalesDocumentsConst.States.CONFIRMED.getUiVal());
        expectedSalesDocument.setTitle("Из торгового зала");
        expectedSalesDocument.setNumber(documentNumber);
        submittedSalesDocumentPage.clickSubmitButton()
                .shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);
    }

}
