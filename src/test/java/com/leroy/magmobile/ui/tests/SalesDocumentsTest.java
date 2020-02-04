package com.leroy.magmobile.ui.tests;

import com.leroy.magmobile.ui.pages.sales.basket.BasketPage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep1Page;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep2Page;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep3Page;
import com.leroy.models.SalesDocumentData;
import com.leroy.magmobile.ui.pages.common.OldSearchProductPage;
import com.leroy.magmobile.ui.pages.sales.*;
import com.leroy.magmobile.ui.AppBaseSteps;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

public class SalesDocumentsTest extends AppBaseSteps {

    @Test(description = "C3201029 Создание документа продажи")
    public void testC3201029() throws Exception {
        // TODO нужен Юзер не от 32 магазина
        // Step #1
        log.step("На главном экране выберите раздел Документы продажи");
        MainSalesDocumentsPage salesDocumentsPage = loginAndGoTo(MainSalesDocumentsPage.class);
        salesDocumentsPage.verifyRequiredElements();

        // Step #2
        log.step("Нажмите 'Создать документ продажи'");
        OldSearchProductPage oldSearchProductPage = salesDocumentsPage.clickCreateSalesDocumentButton();
        oldSearchProductPage.verifyRequiredElements();

        // Step #3
        String inputDataStep3 = "164";
        log.step("Введите 164 код товара");
        oldSearchProductPage.enterTextInSearchFieldAndSubmit(inputDataStep3)
                .shouldCountOfProductsOnPageMoreThan(1)
                .shouldProductCardsContainText(inputDataStep3)
                .shouldProductCardContainAllRequiredElements(0);

        // Step #4
        log.step("Нажмите на мини-карточку товара 16410291");
        AddProductPage addProductPage = oldSearchProductPage.searchProductAndSelect("16410291")
                .verifyRequiredElements();

        // Step #5
        log.step("Нажмите на поле количества");
        addProductPage.clickEditQuantityField()
                .shouldKeyboardVisible();
        addProductPage.shouldEditQuantityFieldIs("1,00")
                .shouldTotalPriceIs(addProductPage.getPrice());

        // Step #6
        log.step("Введите значение 20,5 количества товара");
        String expectedTotalPrice = String.valueOf(
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
        String testPinCode = RandomStringUtils.randomNumeric(5);
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
        expectedSalesDocument.setPrice(expectedTotalPrice);
        expectedSalesDocument.setPin(testPinCode);
        expectedSalesDocument.setDocumentType("Создан");
        expectedSalesDocument.setWhereFrom("Из торгового зала");
        expectedSalesDocument.setNumber("2001" + documentNumber);
        submittedSalesDocumentPage.clickSubmitButton()
                .shouldSalesDocumentByIndexIs(0, expectedSalesDocument);
    }

}
