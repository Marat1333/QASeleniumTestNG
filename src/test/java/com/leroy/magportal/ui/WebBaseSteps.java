package com.leroy.magportal.ui;

import com.leroy.constants.EnvConstants;
import com.leroy.core.configuration.Log;
import com.leroy.magportal.ui.pages.LoginWebPage;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.pages.customers.CustomerPage;
import com.leroy.magportal.ui.pages.picking.PickingPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import io.qameta.allure.Step;

public class WebBaseSteps extends MagPortalBaseTest {

    private String getPageUrl(Class<?> pageClass) {
        String path;
        if (pageClass == CustomerPage.class)
            path = "orders/customers";
        else if (pageClass == SearchProductPage.class)
            path = "orders/catalogproducts";
        else if (pageClass == CartPage.class)
            path = "orders/carts";
        else if (pageClass == EstimatePage.class)
            path = "orders/estimates";
        else if (pageClass == PickingPage.class)
            path = "orders/pickingtask";
        else
            path = "orders/orders_v2";
        return EnvConstants.URL_MAG_PORTAL + "/" + path;
    }

    @Step("Авторизоваться на портале и зайти на страницу {pageClass}")
    public <T extends MagPortalBasePage> T loginAndGoTo(String ldap, String password, Class<T> pageClass) throws Exception {
        getDriver().get(getPageUrl(pageClass));
        new LoginWebPage().logIn(ldap, password);
        T page = pageClass.getConstructor().newInstance();
        String title = page.getCurrentTitle();
        String expectedTitle = "МагПортал";
        if (!title.equals(expectedTitle)) {
            page.reloadPage();
            Log.error("Страница 'Клиентские заказы' не загрузилась. Текущий title = " + title);
        }
        page.waitUntilTitleIs(expectedTitle, 30);
        //page.closeNewFeaturesModalWindowIfExist();
        //До фикса
        //page.selectShopInUserProfile(EnvConstants.BASIC_USER_SHOP_ID);
        return page;
    }

    public <T extends MagPortalBasePage> T loginAndGoTo(Class<T> pageClass) throws Exception {
        return loginAndGoTo(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS, pageClass);
    }

    /**
     * Тест начинается с пустой страницы, т.е. с нуля?
     */
    protected boolean isStartFromScratch() {
        return getDriver().getTitle().isEmpty();
    }

}
