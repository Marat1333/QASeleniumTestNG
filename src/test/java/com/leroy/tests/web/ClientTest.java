package com.leroy.tests.web;

import com.leroy.models.CustomerData;
import com.leroy.pages.web.CreatingCustomerPage;
import com.leroy.pages.web.CustomerPage;
import com.leroy.pages.web.CustomerPersonalInfoPage;
import com.leroy.tests.WebBaseSteps;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

public class ClientTest extends WebBaseSteps {

    @Test(description = "C22783064 Create client via button on client's page")
    public void testC22783064() throws Exception { //TODO Обновить кейс в тестрейл
        // Step #1
        log.step("Open env for testing and add to url 'customers'");
        CustomerPage clientPage = loginAndGoTo(CustomerPage.class);
        clientPage.verifyRequiredElements();

        // Step #2
        log.step("Click on the button 'Создание клиента'");
        CreatingCustomerPage creatingClientPage = clientPage.clickCreateClientButton()
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
                .verifyModalWindowRequiredElements()
                .shouldCustomerRecordsArePresentInModalWindow();

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
        CustomerPersonalInfoPage personalInfoPage = creatingClientPage.clickCreateButtonHappyPath()
                .shouldCustomerDataOnPageIs(customerData);

        // Step #12
        log.step("Check to appear new client in the list of favorites client");
        String phone = customerData.getPersonalPhone() != null ?
                customerData.getPersonalPhone() : customerData.getWorkPhone();
        personalInfoPage.shouldMyRecentlyCustomerIs(0, customerData.getFirstName(), phone);
    }

    // TODO FAKE - FOR DEMO!!!
    @Test(description = "C22846687 Create client via button on client's page")
    public void testC22846687() throws Exception {
        // Step #1
        log.step("Open env for testing and add to url 'customers'");
        CustomerPage clientPage = loginAndGoTo(CustomerPage.class);
        clientPage.verifyRequiredElements();

        // Step #2
        log.step("Click on the button 'Создание клиента'");
        CreatingCustomerPage creatingClientPage = clientPage.clickCreateClientButton()
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
                .verifyModalWindowRequiredElements()
                .shouldCustomerRecordsArePresentInModalWindow();

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
        customerData.setFirstName("Иванов Петр Алексеевич");
        CustomerPersonalInfoPage personalInfoPage = creatingClientPage.clickCreateButtonHappyPath()
                .shouldCustomerDataOnPageIs(customerData);

        // Step #12
        log.step("Check to appear new client in the list of favorites client");
        String phone = customerData.getPersonalPhone() != null ?
                customerData.getPersonalPhone() : customerData.getWorkPhone();
        personalInfoPage.shouldMyRecentlyCustomerIs(0, customerData.getFirstName(), phone);
    }
}
