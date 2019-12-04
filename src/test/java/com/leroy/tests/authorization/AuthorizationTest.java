package com.leroy.tests.authorization;

import com.leroy.core.configuration.Log;
import com.leroy.models.LoginData;
import com.leroy.pages.LoginPage;
import com.leroy.tests.BaseState;
import org.testng.annotations.Test;

public class AuthorizationTest extends BaseState {

    @Test(description = "C0000  aaaaaa")
    public void testC00000() {
        Log.step("1. Try to logIn");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.logIn(new LoginData("my_user", "my_pass"));
        Log.step("2. hhhhhhh");
        String z = "";
    }

}
