package com.leroy.magmobile.ui.tests;

import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.common.SearchProductPage;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import com.leroy.magmobile.ui.pages.sales.SalesPage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketPage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep1Page;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep2Page;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep3Page;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.ActionWithProductModalPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.ImpossibleCreateDocumentWithTopEmModalPage;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.BaseModule;
//import ru.leroymerlin.qa.core.clients.magmobile.MagMobileClient;
//import ru.leroymerlin.qa.core.clients.magmobile.data.ProductItemResponse;
//import ru.leroymerlin.qa.core.clients.magmobile.requests.GetCatalogSearch;


@Guice(modules = {BaseModule.class})
public class MultiFunctionalButtonTest extends AppBaseSteps {

//    @Inject
//    private MagMobileClient apiClient;

    // Получить ЛМ код для обычного продукта без специфичных опций
    private String getAnyLmCodeProductWithoutSpecificOptions() {
        /*GetCatalogSearch params = new GetCatalogSearch()
                .topEM(false);
        List<ProductItemResponse> items = apiClient.searchProductsBy(params).asJson().getItems();
        for (ProductItemResponse item : items) {
            if (item.getAvsDate() == null)
                return item.getLmCode();
        }*/
        return "13452305";
    }

    // Получить ЛМ код для продукта с AVS
    private String getAnyLmCodeProductWithAvs() {
        /*GetCatalogSearch params = new GetCatalogSearch()
                .topEM(false);
        List<ProductItemResponse> items = apiClient.searchProductsBy(params).asJson().getItems();
        for (ProductItemResponse item : items) {
            if (item.getAvsDate() != null)
                return item.getLmCode();
        }*/
        return "82014172";
    }

    // Получить ЛМ код для продукта с опцией TopEM
    private String getAnyLmCodeProductWithTopEM() {
        /*GetCatalogSearch params = new GetCatalogSearch()
                .topEM(true)
                .shopId(EnvConstants.BASIC_USER_SHOP_ID);
        List<ProductItemResponse> items = apiClient.searchProductsBy(params).asJson().getItems();
        for (ProductItemResponse item : items) {
            if (item.getAvsDate() == null)
                return item.getLmCode();
        }
        if (items.size() > 0)
            return items.get(0).getLmCode();*/
        return "82138074";
    }

    // Product Types
    private enum ProductTypes {
        NORMAL, AVS, TOP_EM;
    }

    @Test(description = "C3201023 Создание документа продажи")
    public void testC3201023() throws Exception {
        testCreateSalesDocument(getAnyLmCodeProductWithoutSpecificOptions(), ProductTypes.NORMAL);
    }

    @Test(description = "C22846947 Создание документа продажи с товаром AVS")
    public void testC22846947() throws Exception {
        testCreateSalesDocument(getAnyLmCodeProductWithAvs(), ProductTypes.AVS);
    }

    @Test(description = "C22846948 Создание документа продажи с товаром Топ-EM")
    public void testC22846948() throws Exception {
        testCreateSalesDocument(getAnyLmCodeProductWithTopEM(), ProductTypes.TOP_EM);
    }

    // ---------------------- TYPICAL TESTS FOR THIS CLASS -------------------//

    private void testCreateSalesDocument(String lmCode, ProductTypes productType) throws Exception {
        // Pre-condition
        SalesPage salesPage = loginAndGoTo(SalesPage.class);

        // Step #1
        log.step("Нажмите в поле поиска");
        SearchProductPage searchPage = salesPage.clickSearchBar(false);
        searchPage.shouldKeyboardVisible();
        searchPage.verifyRequiredElements();

        // Step #2
        log.step("Введите ЛМ код товара (напр., " + lmCode + ")");
        searchPage.enterTextInSearchFieldAndSubmit(lmCode);
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage(context)
                .verifyRequiredElements(true);

        // Step #3
        log.step("Нажмите на кнопку Действия с товаром");
        ActionWithProductModalPage actionWithProductModalPage = productDescriptionPage.clickActionWithProductButton()
                .verifyRequiredElements(productType.equals(ProductTypes.AVS));

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