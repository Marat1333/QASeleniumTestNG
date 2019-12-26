package com.leroy.pages.web;

import com.leroy.constants.EnvConstants;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.models.UserData;

public class LoginWebPage extends BaseWebPage {

    public LoginWebPage(TestContext context) {
        super(context);
    }

    @WebFindBy(id = "Username")
    private EditBox usernameFld;

    @WebFindBy(id = "Password")
    private EditBox passwordFld;

    @WebFindBy(xpath = "//*[@value='login']")
    private EditBox loginBtn;

    public void logIn(UserData loginData) {
        usernameFld.clearAndFill(loginData.getUserName());
        passwordFld.clearAndFill(loginData.getPassword());
        loginBtn.click();
    }

    public void logIn() {
        logIn(new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS));
    }

}
