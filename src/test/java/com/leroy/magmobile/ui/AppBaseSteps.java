package com.leroy.magmobile.ui;

import com.leroy.constants.EnvConstants;
import com.leroy.core.configuration.Log;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.pages.ChromeCertificateErrorPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.LoginAppPage;
import com.leroy.magmobile.ui.pages.common.BottomMenuPage;
import com.leroy.magmobile.ui.pages.more.MorePage;
import com.leroy.magmobile.ui.pages.more.UserProfilePage;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep1Page;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.ActionWithProductModalPage;
import com.leroy.magmobile.ui.pages.support.SupportPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magportal.ui.pages.LoginWebPage;
import com.leroy.models.UserData;
import com.leroy.temp_ui.MagMobileBaseState;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AppBaseSteps extends MagMobileBaseState {

    /**
     * Иногда нам нужен пользователь с выбранным 35 магазиным (новый интерфейс)
     * Иногда нам нужен пользователь с выбранным магазином, где старый интерфейс
     */
    protected enum LoginType {
        USER_WITH_NEW_INTERFACE_LIKE_35_SHOP,
        USER_WITH_OLD_INTERFACE
    }

    public <T> T loginAndGoTo(LoginType loginType, UserData userData, Class<? extends BaseAppPage> pageClass) throws Exception {
        AndroidDriver<MobileElement> androidDriver = (AndroidDriver<MobileElement>) driver;
        Element redirectBtn = new Element(driver, By.xpath("//*[@resource-id='buttonRedirect']"));
        new LoginAppPage(context).clickLoginButton();
        /// If the Chrome starts first time and we can see pop-up windows
        boolean moon = false;
        Element termsAcceptBtn = new Element(driver,
                By.id("com.android.chrome:id/terms_accept"));
        if (termsAcceptBtn.isVisible(5)) {
            Log.debug("Accept & Continue button is visible");
            termsAcceptBtn.click();
            moon = true;
            driver.findElement(By.id("com.android.chrome:id/next_button")).click();
            //driver.findElement(By.id("com.android.chrome:id/negative_button")).click();
        }
        new WebDriverWait(this.driver, 30).until(
                a -> androidDriver.getContextHandles().size() > 1);
        ///
        boolean needToClickRedirectBtn;
        try {
            needToClickRedirectBtn = !moon && redirectBtn.isVisible(2);
        } catch (WebDriverException err) {
            needToClickRedirectBtn = false;
        }
        if (needToClickRedirectBtn) {
            Log.debug("Click Redirect Button");
            redirectBtn.click();
        } else {
            Log.debug("Redirect Button is not visible");
            if (moon) {
                new ChromeCertificateErrorPage(context).skipSiteSecureError();
                androidDriver.context("WEBVIEW_chrome");
                new LoginWebPage(context).logIn(userData);
                androidDriver.context("NATIVE_APP");
                try {
                    redirectBtn.click();
                } catch (NoSuchElementException err) {
                    // Если получили ошибку HTTP ERROR 500
                    androidDriver.context("WEBVIEW_chrome");
                    LoginWebPage loginWebPage = new LoginWebPage(context);
                    //String consoleErrors = loginWebPage.getJSErrorsFromConsole();
                    //Log.error("CONSOLE ERRORS:" + consoleErrors);
                    loginWebPage.reloadPage();
                    androidDriver.context("NATIVE_APP");
                    redirectBtn.click();
                }
            } else {
                if (new Element(driver, By.xpath("//*[@resource-id='Username']")).isVisible(1)) {
                    androidDriver.context("WEBVIEW_chrome");
                    LoginWebPage loginWebPage = new LoginWebPage(context);
                    loginWebPage.logIn(userData);
                    androidDriver.context("NATIVE_APP");
                }
            }
        }

        MainProductAndServicesPage mainProductAndServicesPage = new MainProductAndServicesPage(context);
        UserProfilePage userProfilePage = null;
        if (loginType != null) {
            switch (loginType) {
                case USER_WITH_OLD_INTERFACE:
                    context.setUserShopId("5");
                    userProfilePage = setShopAndDepartmentForUser(mainProductAndServicesPage, "5", "01");
                    break;
                case USER_WITH_NEW_INTERFACE_LIKE_35_SHOP:
                    context.setUserShopId("35");
                    userProfilePage = setShopAndDepartmentForUser(mainProductAndServicesPage, "35", "01");
                    break;
            }
        }
        if (pageClass.equals(MainProductAndServicesPage.class)) {
            if (userProfilePage != null)
                return (T) userProfilePage.goToSales();
            return (T) mainProductAndServicesPage;
        } else if (pageClass.equals(MainSalesDocumentsPage.class)) {
            if (userProfilePage != null)
                return (T) userProfilePage.goToSales().goToSalesDocumentsSection();
            return (T) mainProductAndServicesPage.goToSalesDocumentsSection();
        } else if (pageClass.equals(WorkPage.class)) {
            return (T) mainProductAndServicesPage.goToWork();
        } else if (pageClass.equals(SupportPage.class)) {
            return (T) mainProductAndServicesPage.goToSupport();
        } else if (pageClass.equals(MorePage.class)) {
            return (T) mainProductAndServicesPage.goToMoreSection();
        } else {
            throw new IllegalArgumentException("Переход на страницу " + pageClass.getName() +
                    " еще не реализован через класс TopMenuPage");
        }
    }

    public <T> T loginAndGoTo(Class<? extends BaseAppPage> pageClass) throws Exception {
        return loginAndGoTo(null, new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS), pageClass);
    }

    /**
     * Используй этот метод, когда нам не важен сам пользователь, но важен тип магазина, который будет выбран
     */
    public <T> T loginAndGoTo(LoginType loginType, Class<? extends BaseAppPage> pageClass) throws Exception {
        return loginAndGoTo(loginType,
                new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS), pageClass);
    }

    // Pre-condition steps

    /**
     * Login in an application, create a draft of a sales document for product
     * and return to the Sales page
     *
     * @param lmCode - lmCode of the product
     * @return - Document number of the draft
     */
    protected String loginInAndCreateDraftSalesDocument(String lmCode) throws Exception {
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(
                LoginType.USER_WITH_OLD_INTERFACE, MainProductAndServicesPage.class);
        mainProductAndServicesPage.clickSearchBar(false)
                .enterTextInSearchFieldAndSubmit(lmCode);

        new ProductDescriptionPage(context).clickActionWithProductButton();
        ActionWithProductModalPage modalPage = new ActionWithProductModalPage(context);
        BasketStep1Page basketStep1Page = modalPage.startToCreateSalesDocument()
                .clickAddButton();
        String documentNumber = basketStep1Page.getDocumentNumber();
        basketStep1Page.clickBackButton()
                .returnBack().backToSalesPage();
        return documentNumber;
    }

    @Step("Установить магазин {shop} и отдел {department} для пользователя")
    protected UserProfilePage setShopAndDepartmentForUser(BottomMenuPage page, String shop, String department)
            throws Exception {
        return page.goToMoreSection()
                .goToUserProfile()
                .goToEditShopForm()
                .searchForShopAndSelectById(shop)
                .goToEditDepartmentForm()
                .selectDepartmentById(department);
    }

}
