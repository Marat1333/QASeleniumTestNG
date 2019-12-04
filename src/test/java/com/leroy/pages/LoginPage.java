package com.leroy.pages;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BasePageObject;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.models.LoginData;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePageObject {

    public String TITLE = "";

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @AppFindBy(accessibilityId = "ldap")
    @WebFindBy(id = "Username")
    public EditBox usernameFld;

    @AppFindBy(accessibilityId = "password")
    @WebFindBy(id = "Password")
    public EditBox passwordFld;

    public void logIn(LoginData loginData) {
        usernameFld.click();
        usernameFld.clearAndFill(loginData.getUserName());
        passwordFld.click();
        passwordFld.clearAndFill(loginData.getPassword());
    }

}
