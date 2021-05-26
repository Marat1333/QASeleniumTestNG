package com.leroy.magportal.ui.pages;

import com.leroy.constants.EnvConstants;
import com.leroy.core.configuration.Log;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;

public class LoginWebPage extends BaseWebPage {

    private EditBox usernameFld = E("#Username", "Поле для ввода username", EditBox.class);

    private EditBox passwordFld = E("#Password", "Поле для ввода password", EditBox.class);

    private Button loginBtn = E("//*[@value='login']", "Кнопка 'Войти'", Button.class);

    public void logIn(String ldap, String password) {
        usernameFld.clearAndFill(ldap);
        passwordFld.clearAndFill(password);
        Log.info("|" + usernameFld.getText() + "|");
        Log.info("|" + passwordFld.getText() + "|");
        loginBtn.clickJS();
    }

    public boolean isLoginFormVisible() {
        return usernameFld.isVisible();
    }

    public void logIn() {
        logIn(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS);
    }

}
