package com.leroy.pages;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.models.UserData;
import com.leroy.pages.app.SalesPage;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BaseWebPage {

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

    @AppFindBy(accessibilityId = "AuthLoginButton")
    @WebFindBy(xpath = "//button[@value='login']")
    public Button loginBtn;

    // Error message
    @WebFindBy(xpath = "//div[@class='alert alert-danger']/strong")
    public Element errorTitle;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='AuthScreen__error_wrongLoginOrPass']//android.widget.TextView")
    @WebFindBy(xpath = "//div[@class='danger validation-summary-errors']")
    public Element errorBody;

    public SalesPage logIn(UserData loginData) {
        usernameFld.clearAndFill(loginData.getUserName());
        passwordFld.clearAndFill(loginData.getPassword());
        loginBtn.click();
        return new SalesPage(driver);
    }

}
