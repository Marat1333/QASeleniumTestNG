package com.leroy.magportal.ui.tests;

import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.models.customers.CustomerWebData;
import com.leroy.magportal.ui.pages.customers.CreateCustomerForm;
import com.leroy.magportal.ui.pages.customers.CreatingCustomerPage;
import com.leroy.magportal.ui.pages.customers.CustomerPage;
import com.leroy.magportal.ui.pages.customers.CustomerPersonalInfoPage;
import io.qameta.allure.Issue;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

public class ClientTest extends WebBaseSteps {

    @Issue("PAO-465")
    @Test(description = "C22783064 Create client via button on client's page")
    public void testC22783064() throws Exception {
        // Step #1
        step("Open env for testing and add to url 'customers'");
        CustomerPage clientPage = loginAndGoTo(CustomerPage.class);
        clientPage.verifyRequiredElements();

        // Step #2
        step("Click on the button 'Создание клиента'");
        CreatingCustomerPage creatingClientPage = clientPage.clickCreateClientButton();
        CreateCustomerForm createCustomerForm = creatingClientPage.getCreateCustomerForm();
        createCustomerForm.verifyRequiredElements();

        // Step #3
        step("Click on the button 'Создать'");
        createCustomerForm.clickConfirmButton();
        createCustomerForm.shouldAllRequiredFieldsHighlightedInRed();

        // Step #4
        step("Enter to field 'Имя' Latin characters and remove focus from the field");
        createCustomerForm.enterTextInFirstNameInputField("SomeText")
                .clickConfirmButton();
        createCustomerForm.shouldErrorTooltipUnderFirstNameFldHasValidText();

        // Step #5
        step("Enter to field 'Телефон' 5 characters and remove focus from the field");
        createCustomerForm.enterTextInPhoneInputField(RandomStringUtils.randomNumeric(5))
                .clickConfirmButton();
        createCustomerForm.shouldErrorTooltipUnderPhoneFldHasValidText();

        // Step #6
        step("Click on 'Показать все поля'");
        createCustomerForm.clickShowAllFieldsButton()
                .verifyAllAdditionalFields();

        // Step #7
        step("Scroll to down of the page and click on 'Скрыть все поля'");
        createCustomerForm.clickHideAllFieldsButton()
                .verifyRequiredElements();

        // Step #8
        step("Enter to field 'Телефон' number of '1111111111' and remove focus from the field");
        createCustomerForm.enterTextInPhoneInputField("1111111111")
                .verifyModalWindowRequiredElements()
                .shouldCustomerRecordsArePresentInModalWindow();

        // Step #9
        step("Click on button 'Вернуться'");
        createCustomerForm.clickModalWindowReturnButton()
                .verifyRequiredElements()
                .shouldModalWindowInvisible();

        // Step #10
        step("Entering correct fields for input text and choice gender, type of the phone");
        CustomerWebData customerData = new CustomerWebData().setRandomRequiredData();
        createCustomerForm.enterCustomerData(customerData)
                .shouldBeEnteredDataMatchThis(customerData);

        // Step #11
        step("Click on the button 'Создать'");
        createCustomerForm.clickConfirmButton();
        CustomerPersonalInfoPage personalInfoPage = new CustomerPersonalInfoPage()
                .shouldCustomerDataOnPageIs(customerData);

        // Step #12
        step("Check to appear new client in the list of favorites client");
        String phone = customerData.getPhoneNumber();
        personalInfoPage.shouldMyRecentlyCustomerIs(0, customerData.getFirstName(), phone);
    }

    @Test(description = "C22783068 Create client via modal window after search")
    public void testC22783068() throws Exception {
        // Pre-condition
        String phoneNumber = apiClientProvider.findUnusedPhoneNumber();
        String email = RandomStringUtils.randomAlphanumeric(8) + "@mail.com";
        CustomerPage clientPage = loginAndGoTo(CustomerPage.class);

        // Step #1
        step("Enter phone1 to search string and click on magnifier icon");
        CustomerPage customerPage = clientPage.searchClient(phoneNumber)
                .verifyClientNotFoundForm();

        // Step #2
        step("Click on the button 'Создать клиента'");
        CustomerWebData data = new CustomerWebData();
        data.setPhoneNumber(phoneNumber);
        CreatingCustomerPage creatingCustomerPage = customerPage.clickNotFoundCreateClientButton();
        CreateCustomerForm createCustomerForm = creatingCustomerPage.getCreateCustomerForm();
        createCustomerForm.verifyRequiredElements()
                .shouldBeEnteredDataMatchThis(data);

        // Step #3
        step("Close creation mode and return to main page");
        customerPage = createCustomerForm.clickBackButton()
                .verifyRequiredElements();

        // Step #4
        step("For search field change phone to email");
        customerPage.selectSearchOption(CustomerPage.EMAIL_OPTION)
                .shouldCurrentSearchTypeLabelIs(CustomerPage.EMAIL_OPTION);

        // Step #5
        step("Enter email1 to search string and click on magnifier icon");
        customerPage.searchClient(email)
                .verifyClientNotFoundForm();

        // Step #6
        step("Click on the button 'Создать клиента'");
        data = new CustomerWebData();
        data.setEmail(email);
        createCustomerForm = customerPage.clickNotFoundCreateClientButton().getCreateCustomerForm()
                .verifyRequiredElements(true)
                .shouldBeEnteredDataMatchThis(data);

        // Step #7
        step("Entering correct fields for input text and choice gender, type of the phone");
        data = new CustomerWebData().setRandomRequiredData();
        data.setEmail(email);
        data.setPersonalEmail(true);
        createCustomerForm.enterCustomerData(data)
                .shouldBeEnteredDataMatchThis(data);

        // Step #8
        step("Click on the button \"Создать\"");
        createCustomerForm.clickConfirmButton();
        CustomerPersonalInfoPage personalInfoPage = new CustomerPersonalInfoPage()
                .verifyRequiredElements()
                .shouldCustomerDataOnPageIs(data);

        // Step #9
        step("Check to appear new client in the list of favorites client");
        String phone = data.getPhoneNumber();
        // TODO BUG
        // personalInfoPage.shouldMyRecentlyCustomerIs(0, data.getFirstName(), phone);
    }
}
