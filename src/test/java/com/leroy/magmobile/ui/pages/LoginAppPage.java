package com.leroy.magmobile.ui.pages;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class LoginAppPage extends CommonMagMobilePage {

    public LoginAppPage(Context context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "Button")
    public Button loginBtn;


    /* ------------------------- ACTIONS -------------------------- */

    @Step("Нажмите кнопку 'Войти'")
    public void clickLoginButton() throws Exception {
        loginBtn.click();
        //E("$AuthScreen__btn_getVersionNumber").waitForInvisibility();
    }
}
