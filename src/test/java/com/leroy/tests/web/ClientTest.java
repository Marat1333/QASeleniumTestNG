package com.leroy.tests.web;

import com.leroy.models.CustomerData;
import com.leroy.pages.web.ClientPage;
import com.leroy.pages.web.CreatingClientPage;
import com.leroy.pages.web.LoginWebPage;
import com.leroy.tests.BaseState;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

public class ClientTest extends BaseState {

    @Test(description = "C22783064 Create client via button on client's page")
    public void testC22783064() throws Exception {
        // Step #1
        new LoginWebPage(context).logIn();
        driver.get("https://dev.prudevlegowp.hq.ru.corp.leroymerlin.com/customers");

        log.step("Open env for testing and add to url 'customers'");
        ClientPage clientPage = new ClientPage(context).verifyRequiredElements();

        // Step #2
        log.step("Click on the button 'Создание клиента'");
        CreatingClientPage creatingClientPage = clientPage.clickCreateClientButton()
                .verifyRequiredElements();

        // Step #3
        log.step("Click on the button 'Создать'");
        creatingClientPage.clickCreateButtonNegativePath()
                .shouldAllRequiredFieldsHighlightedInRed();

        // Step #4
        log.step("Enter to field 'Имя' Latin characters and remove focus from the field");
        creatingClientPage.enterTextInFirstNameInputField("SomeText")
                .clickCreateButtonNegativePath()
                .shouldErrorTooltipUnderFirstNameFldHasValidText();

        // Step #5
        log.step("Enter to field 'Телефон' 5 characters and remove focus from the field");
        creatingClientPage.enterTextInPhoneInputField(RandomStringUtils.randomNumeric(5))
                .clickCreateButtonNegativePath()
                .shouldErrorTooltipUnderPhoneFldHasValidText();

        // Step #6
        log.step("Click on 'Показать все поля'");
        creatingClientPage.clickShowAllFieldsButton()
                .verifyAllAdditionalFields();

        // Step #7
        log.step("Scroll to down of the page and click on 'Скрыть все поля'");
        creatingClientPage.clickHideAllFieldsButton()
                .verifyRequiredElements();

        // Step #8
        log.step("Enter to field 'Телефон' number of '1111111111' and remove focus from the field");
        creatingClientPage.enterTextInPhoneInputField("1111111111")
                .verifyModalWindowRequiredElements();
        // TODO надо добавить проверку на существующие записи

        // Step #9
        log.step("Click on button 'Вернуться'");
        creatingClientPage.clickModalWindowReturnButton()
                .verifyRequiredElements()
                .shouldModalWindowInvisible();

        // Step #10
        log.step("Entering correct fields for input text and choice gender, type of the phone");
        CustomerData customerData = new CustomerData().setRandomRequiredData();
        creatingClientPage.enterRequiredCustomerData(customerData)
                .shouldBeEnteredDataMatchThis(customerData);

        // Step #11
        log.step("Click on the button 'Создать'");
        creatingClientPage.clickCreateButtonHappyPath();
        String s = "";


    }
}
