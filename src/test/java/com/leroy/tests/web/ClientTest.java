package com.leroy.tests.web;

import com.leroy.pages.LoginPage;
import com.leroy.pages.web.ClientPage;
import com.leroy.pages.web.LoginWebPage;
import com.leroy.tests.BaseState;
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
        clientPage.clickCreateClientButton()
                .verifyRequiredElements();

        // Step #3
        log.step("Click on the button 'Создать'");



    }
}
