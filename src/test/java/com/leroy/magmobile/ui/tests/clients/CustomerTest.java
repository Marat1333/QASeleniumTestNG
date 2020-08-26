package com.leroy.magmobile.ui.tests.clients;

import com.leroy.constants.Gender;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.constants.TestDataConstants;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.pages.customers.MainCustomerPage;
import com.leroy.magmobile.ui.pages.customers.NewCustomerInfoPage;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.customers.SuccessCustomerPage;
import com.leroy.utils.RandomUtil;
import org.testng.annotations.Test;

import java.util.Random;

public class CustomerTest extends AppBaseSteps {

    @Test(description = "C3201018 Создание клиента (физ. лицо)")
    public void testCreateCustomer() throws Exception {
        MainCustomerPage mainCustomerPage = loginAndGoTo(MainCustomerPage.class);

        // Step 1
        step("Нажмите на кнопку Создать нового клиента");
        NewCustomerInfoPage newCustomerInfoPage = mainCustomerPage.clickCreateNewCustomer()
                .verifyRequiredElements(false);

        // Steps 2-4
        step("Введите имя нового клиента");
        String customerFirstName = RandomUtil.randomCyrillicCharacters(7);
        newCustomerInfoPage.editCustomerFirstName(customerFirstName, true);

        // Step 5
        step("Выберите пол клиента");
        Gender gender = new Random().nextBoolean() ? Gender.MALE : Gender.FEMALE;
        newCustomerInfoPage.selectGender(gender, true);

        // Steps 6-9
        step("Ведите новый номер телефона");
        String phone = RandomUtil.randomPhoneNumber();
        newCustomerInfoPage.editPhoneNumber(phone, true);

        // Step 10
        step("Нажмите на Показать все поля");
        newCustomerInfoPage.clickShowAllFieldsButton()
                .verifyRequiredElements(true);

        // Step 11
        step("Нажмите на Скрыть дополнительные поля");
        newCustomerInfoPage.clickHideAdditionalFieldsButton()
                .verifyRequiredElements(false);

        // Step 12
        step("Нажмите на Создать");
        SuccessCustomerPage successCustomerPage = newCustomerInfoPage.clickSubmitButton();
        successCustomerPage.verifyRequiredElements();

        // Step 13
        step("Нажмите на Перейти к списку клиентов");
        MagCustomerData customerData = new MagCustomerData();
        customerData.setPhone(phone);
        customerData.setName(customerFirstName);
        mainCustomerPage = successCustomerPage.clickGoToCustomerListButton();
        mainCustomerPage.shouldRecentCustomerIs(1, customerData);
    }

    @Test(description = "C3201020 Поиск клиента по телефону (физ. лицо)")
    public void testSearchForIndividualCustomerByPhone() throws Exception {
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;

        MainCustomerPage mainCustomerPage = loginAndGoTo(MainCustomerPage.class);

        // Step 1
        step("Нажмите на поле Поиск клиента");
        SearchCustomerPage searchCustomerPage = mainCustomerPage.clickSearchCustomerField()
                .verifyRequiredElements();

        // Step 2
        step("Введите номер клиента");
        searchCustomerPage.searchCustomerByPhone(customerData.getPhone(), false);
        searchCustomerPage.shouldFirstCustomerIs(customerData);
    }

}
