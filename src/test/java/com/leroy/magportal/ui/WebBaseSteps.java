package com.leroy.magportal.ui;

import com.leroy.constants.EnvConstants;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.magportal.ui.pages.LoginWebPage;
import com.leroy.magportal.ui.pages.OrdersPage;
import com.leroy.magmobile.ui.MagMobileBaseTest;
import io.qameta.allure.Step;

public class WebBaseSteps extends MagPortalBaseTest {

    @Step("Авторизоваться на портале и зайти на страницу {pageClass}")
    public <T> T loginAndGoTo(String ldap, String password, Class<? extends BaseWebPage> pageClass) throws Exception {
        new LoginWebPage(context).logIn(ldap, password);
        return new OrdersPage(context).closeNewFeaturesModalWindowIfExist()
                .goToPage(pageClass);
    }

    public <T> T loginAndGoTo(Class<? extends BaseWebPage> pageClass) throws Exception {
        return loginAndGoTo(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS, pageClass);
    }

}
