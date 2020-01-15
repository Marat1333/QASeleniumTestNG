package com.leroy.magmobile.ui.tests;

import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.common.SearchProductPage;
import com.leroy.magmobile.ui.pages.sales.*;
import com.leroy.magmobile.ui.pages.sales.basket.BasketPage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep1Page;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep2Page;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep3Page;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.ActionWithProductModalScreen;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.Test;

public class MultiFunctionalButtonTest extends AppBaseSteps {

    private static final String LM_CODE_EXISTING_PRODUCT = "13452305";

    @Test(description = "C3201023 Создание документа продажи")
    public void testC3201023() throws Exception {
        // Pre-condition
        SalesPage salesPage = loginAndGoTo(SalesPage.class);

        // Step #1
        log.step("Нажмите в поле поиска");
        SearchProductPage searchPage = salesPage.clickSearchBar(false);
        searchPage.shouldKeyboardVisible();
        searchPage.verifyRequiredElements();

        // Step #2
        log.step("Введите ЛМ код товара (напр., " + LM_CODE_EXISTING_PRODUCT + ")");
        searchPage.enterTextInSearchFieldAndSubmit(LM_CODE_EXISTING_PRODUCT);
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage(context)
                .verifyRequiredElements(true);

        // Step #3
        log.step("Нажмите на кнопку Действия с товаром");
        ActionWithProductModalScreen actionWithProductModalPage = productDescriptionPage.clickActionWithProductButton()
                .verifyRequiredElements();

        // Step #4
        log.step("Нажмите Добавить в документ продажи");
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

        // Step #11
        log.step("Нажмите кнопку Подтвердить");
        basketStep3Page.clickSubmitButton()
                .verifyRequiredElements()
                .shouldPinCodeIs(testPinCode)
                .shouldDocumentNumberIs(documentNumber);
    }
}