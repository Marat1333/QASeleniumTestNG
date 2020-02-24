package com.leroy.magportal.ui;

import com.leroy.constants.EnvConstants;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.magportal.ui.pages.LoginWebPage;
import com.leroy.magportal.ui.pages.OrdersPage;
import com.leroy.models.UserData;
import com.leroy.temp_ui.MagMobileBaseState;
import io.qameta.allure.Step;

public class WebBaseSteps extends MagMobileBaseState {

    @Step("Авторизоваться на портале и зайти на страницу {pageClass}")
    public <T> T loginAndGoTo(UserData userData, Class<? extends BaseWebPage> pageClass) throws Exception {
        new LoginWebPage(context).logIn(userData);
        return new OrdersPage(context).closeNewFeaturesModalWindowIfExist()
                .goToPage(pageClass);
    }

    public <T> T loginAndGoTo(Class<? extends BaseWebPage> pageClass) throws Exception {
        return loginAndGoTo(new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS), pageClass);
    }

}
