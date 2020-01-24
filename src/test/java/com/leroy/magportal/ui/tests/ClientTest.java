package com.leroy.magportal.ui.tests;

import com.leroy.models.CustomerData;
import com.leroy.magportal.ui.pages.CreatingCustomerPage;
import com.leroy.magportal.ui.pages.CustomerPage;
import com.leroy.magportal.ui.pages.CustomerPersonalInfoPage;
import com.leroy.magportal.ui.WebBaseSteps;
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
        creatingClientPage.enterCustomerData(customerData)
                .shouldBeEnteredDataMatchThis(customerData);

        // Step #11
        log.step("Click on the button 'Создать'");
        CustomerPersonalInfoPage personalInfoPage = creatingClientPage.clickCreateButtonHappyPath()
                .shouldCustomerDataOnPageIs(customerData);

        // Step #12
        log.step("Check to appear new client in the list of favorites client");
        String phone = customerData.getPhoneNumber();
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
        creatingClientPage.enterCustomerData(customerData)
                .shouldBeEnteredDataMatchThis(customerData);

        // Step #11
        log.step("Click on the button 'Создать'");
        customerData.setFirstName("Иванов Петр Алексеевич");
        CustomerPersonalInfoPage personalInfoPage = creatingClientPage.clickCreateButtonHappyPath()
                .shouldCustomerDataOnPageIs(customerData);

        // Step #12
        log.step("Check to appear new client in the list of favorites client");
        String phone = customerData.getPhoneNumber();
        personalInfoPage.shouldMyRecentlyCustomerIs(0, customerData.getFirstName(), phone);
    }

    @Test(description = "C22783068 Create client via modal window after search")
    public void testC22783068() throws Exception { //TODO Обновить кейс в тестрейл
        // Pre-condition
        String phoneNumber = RandomStringUtils.randomNumeric(10);
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.com";
        CustomerPage clientPage = loginAndGoTo(CustomerPage.class);

        // Step #1
        log.step("Enter phone1 to search string and click on magnifier icon");
        CustomerPage customerPage = clientPage.searchClient(phoneNumber)
                .verifyClientNotFoundForm();

        // Step #2
        log.step("Click on the button 'Создать клиента'");
        CustomerData data = new CustomerData();
        data.setPhoneNumber(phoneNumber);
        CreatingCustomerPage creatingCustomerPage = customerPage.clickNotFoundCreateClientButton()
                .verifyRequiredElements()
                .shouldBeEnteredDataMatchThis(data);

        // Step #3
        log.step("Close creation mode and return to main page");
        customerPage = creatingCustomerPage.clickBackButton()
                .verifyRequiredElements();

        // Step #4
        log.step("For search field change phone to email");
        customerPage.selectSearchOption(CustomerPage.EMAIL_OPTION)
                .shouldCurrentSearchTypeLabelIs(CustomerPage.EMAIL_OPTION);

        // Step #5
        log.step("Enter email1 to search string and click on magnifier icon");
        customerPage.searchClient(email)
                .verifyClientNotFoundForm();

        // Step #6
        log.step("Click on the button 'Создать клиента'");
        data = new CustomerData();
        data.setEmail(email);
        creatingCustomerPage = customerPage.clickNotFoundCreateClientButton()
                .verifyRequiredElements(true)
                .shouldBeEnteredDataMatchThis(data);

        // Step #7
        log.step("Entering correct fields for input text and choice gender, type of the phone");
        data = new CustomerData().setRandomRequiredData();
        data.setEmail(email);
        data.setPersonalEmail(true);
        creatingCustomerPage.enterCustomerData(data)
                .shouldBeEnteredDataMatchThis(data);

        // Step #8
        log.step("Click on the button \"Создать\"");
        CustomerPersonalInfoPage personalInfoPage = creatingCustomerPage.clickCreateButtonHappyPath()
                .verifyRequiredElements()
                .shouldCustomerDataOnPageIs(data);

        // Step #9
        log.step("Check to appear new client in the list of favorites client");
        String phone = data.getPhoneNumber();
        personalInfoPage.shouldMyRecentlyCustomerIs(0, data.getFirstName(), phone);
    }
}