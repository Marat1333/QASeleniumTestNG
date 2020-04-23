package com.leroy.magportal.ui;

import com.leroy.constants.EnvConstants;
import com.leroy.core.configuration.Log;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.magportal.ui.pages.LoginWebPage;
import com.leroy.magportal.ui.pages.OrdersPage;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

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

    public <T> T switchToTab(int tabIndex) {
        if (tabIndex<0){
            throw new IllegalArgumentException("tabIndex can`t be less then 0");
        }
        List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
        if (windowHandles.size()==0){
            throw new NullPointerException("There is no tabs");
        } else if (windowHandles.size()==1) {
            Log.warn("There is only 1 tab");
            return null;
        } else {
            driver.switchTo().window(windowHandles.get(tabIndex));
            return (T) new MagPortalBasePage(context);
        }
    }

}
