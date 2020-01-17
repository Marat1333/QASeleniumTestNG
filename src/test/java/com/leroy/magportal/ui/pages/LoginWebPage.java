package com.leroy.magportal.ui.pages;

import com.leroy.constants.EnvConstants;
import com.leroy.core.TestContext;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.models.UserData;

public class LoginWebPage extends BaseWebPage {

    public LoginWebPage(TestContext context) {
        super(context);
    }

    private EditBox usernameFld = E("#Username", "Поле для ввода username", EditBox.class);

    private EditBox passwordFld = E("#Password", "Поле для ввода password", EditBox.class);

    private Button loginBtn = E("//*[@value='login']", "Кнопка 'Войти'", Button.class);

    public void logIn(UserData loginData) {
        usernameFld.clearAndFill(loginData.getUserName());
        passwordFld.clearAndFill(loginData.getPassword());
        loginBtn.click();
    }

    public void logIn() {
        logIn(new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS));
    }

}
