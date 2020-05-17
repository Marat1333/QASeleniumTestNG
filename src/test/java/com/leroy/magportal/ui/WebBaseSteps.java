package com.leroy.magportal.ui;

import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.LoginWebPage;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.pages.customers.CustomerPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import io.qameta.allure.Step;

public class WebBaseSteps extends MagPortalBaseTest {

    private String getPageUrl(Class<?> pageClass) {
        String path;
        if (pageClass == CustomerPage.class)
            path = "customers";
        else if (pageClass == SearchProductPage.class)
            path = "catalogproducts";
        else if (pageClass == CartPage.class)
            path = "carts";
        else if (pageClass == EstimatePage.class)
            path = "estimates";
        else
            path = "orders_v2";
        return EnvConstants.URL_MAG_PORTAL + "/" + path;
    }

    @Step("Авторизоваться на портале и зайти на страницу {pageClass}")
    public <T extends MenuPage> T loginAndGoTo(String ldap, String password, Class<T> pageClass) throws Exception {
        driver.get(getPageUrl(pageClass));
        new LoginWebPage(context).logIn(ldap, password);
        return (T) pageClass.getConstructor(Context.class).newInstance(context)
                .closeNewFeaturesModalWindowIfExist();
    }

    /*@Step("Авторизоваться на портале и зайти на страницу {pageClass}")
    public <T extends BaseWebPage> T loginAndGoTo(String ldap, String password, Class<T> pageClass) throws Exception {
        new LoginWebPage(context).logIn(ldap, password);
        return new OrdersPage(context).closeNewFeaturesModalWindowIfExist()
                .goToPage(pageClass);
    }*/

    public <T extends MenuPage> T loginAndGoTo(Class<T> pageClass) throws Exception {
        return loginAndGoTo(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS, pageClass);
    }

    /**
     * Тест начинается с пустой страницы, т.е. с нуля?
     */
    protected boolean isStartFromScratch() {
        return driver.getTitle().isEmpty();
    }

}
