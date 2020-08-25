package com.leroy.magmobile.ui.tests.sales;

import com.leroy.constants.Gender;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.pages.customers.CustomerPage;
import com.leroy.magmobile.ui.pages.customers.NewCustomerInfoPage;
import com.leroy.magmobile.ui.pages.customers.SuccessCustomerPage;
import com.leroy.utils.RandomUtil;
import org.testng.annotations.Test;

import java.util.Random;

public class CustomerTest extends AppBaseSteps {

    @Test(description = "C3201018 Создание клиента (физ. лицо)")
    public void testCreateCustomer() throws Exception {
        CustomerPage customerPage = loginAndGoTo(CustomerPage.class);

        // Step 1
        step("Нажмите на кнопку Создать нового клиента");
        NewCustomerInfoPage newCustomerInfoPage = customerPage.clickCreateNewCustomer()
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
        customerPage = successCustomerPage.clickGoToCustomerListButton();
        customerPage.shouldRecentCustomerIs(1, customerData);
    }

}
