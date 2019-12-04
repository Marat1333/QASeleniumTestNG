package com.leroy.tests.authorization;

import com.leroy.core.configuration.Log;
import com.leroy.models.LoginData;
import com.leroy.pages.LoginPage;
import com.leroy.tests.BaseState;
import org.testng.annotations.Test;

public class AuthorizationTest extends BaseState {

    @Test(description = "C3201013 Логин с неверным паролем")
    public void testC3201013() {
        Log.step("1. Введите валидный username (ldap) и невалидный password. Нажмите 'Login'кнопку.");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.logIn(new LoginData("66666666", "my_pass"));
        loginPage.errorBody.waitForVisibility();
        //softAssert.isElementTextEqual(loginPage.errorTitle, "Error")
        softAssert.isElementTextEqual(loginPage.errorBody, "Неверный LDAP или Пароль");
        softAssert.verifyAll();
    }

}
