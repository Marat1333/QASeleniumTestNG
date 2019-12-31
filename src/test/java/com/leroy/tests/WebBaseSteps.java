package com.leroy.tests;

import com.leroy.constants.EnvConstants;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.models.UserData;
import com.leroy.pages.web.LoginWebPage;
import com.leroy.pages.web.OrdersPage;

public class WebBaseSteps extends BaseState {

    public <T> T loginAndGoTo(UserData userData, Class<? extends BaseWebPage> pageClass) throws Exception {
        new LoginWebPage(context).logIn(userData);
        return new OrdersPage(context).closeNewFeaturesModalWindowIfExist()
                .goToPage(pageClass);
    }

    public <T> T loginAndGoTo(Class<? extends BaseWebPage> pageClass) throws Exception {
        return loginAndGoTo(new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS), pageClass);
    }

}
