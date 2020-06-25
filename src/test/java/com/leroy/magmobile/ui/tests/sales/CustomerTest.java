package com.leroy.magmobile.ui.tests.sales;

import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.customers.CustomerPage;
import com.leroy.magmobile.ui.pages.customers.NewCustomerInfoPage;
import org.testng.annotations.Test;

public class CustomerTest extends AppBaseSteps {

    @Test(description = "C3201018 Создание клиента (физ. лицо)")
    public void testCreateCustomer() throws Exception {
        CustomerPage customerPage = loginAndGoTo(CustomerPage.class);

        // Step 1
        step("Нажмите на кнопку Создать нового клиента");
        NewCustomerInfoPage newCustomerInfoPage = customerPage.clickCreateNewCustomer();

        // Steps 2-4
        step("Введите имя нового клиента");

        // Step 5
        step("Выберите пол клиента");

        // Steps 6-8
        step("Ведите новый номер телефона");

        // Step 9
        step("Нажмите на поле галочки.");

        // Step 10
        step("Нажмите на Показать все поля");

        // Step 11
        step("Нажмите на Скрыть дополнительные поля");

        // Step 12
        step("Нажмите на Создать");

        // Step 13
        step("Нажмите на Перейти к списку клиентов");

    }

}
