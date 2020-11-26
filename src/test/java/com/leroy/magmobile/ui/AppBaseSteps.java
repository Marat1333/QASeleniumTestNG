package com.leroy.magmobile.ui;

import com.leroy.constants.EnvConstants;
import com.leroy.core.configuration.Log;
import com.leroy.core.listeners.helpers.RetryAnalyzer;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.pages.ChromeCertificateErrorPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.LoginAppPage;
import com.leroy.magmobile.ui.pages.common.BottomMenuPage;
import com.leroy.magmobile.ui.pages.customers.MainCustomerPage;
import com.leroy.magmobile.ui.pages.more.MorePage;
import com.leroy.magmobile.ui.pages.more.UserProfilePage;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartStep1Page;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.ActionWithProductModalPage;
import com.leroy.magmobile.ui.pages.support.SupportPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magportal.ui.pages.LoginWebPage;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AppBaseSteps extends MagMobileBaseTest {

    public <T extends BaseAppPage> T loginAndGoTo(String userLdap, String password, boolean selectShopAndDepartment,
                                                  Class<T> pageClass) throws Exception {
        try {
            WebDriver driver = getDriver();
            AndroidDriver<MobileElement> androidDriver = (AndroidDriver<MobileElement>) driver;
            Element redirectBtn = new Element(driver, By.xpath("//*[@resource-id='buttonRedirect']"));
            new LoginAppPage().clickLoginButton();
            /// If the Chrome starts first time and we can see pop-up windows
            boolean moon = false;
            Element termsAcceptBtn = new Element(driver,
                    By.id("com.android.chrome:id/terms_accept"));
            if (termsAcceptBtn.isVisible(6)) {
                Log.debug("Accept & Continue button is visible");
                termsAcceptBtn.click();
                termsAcceptBtn.waitForInvisibility();
                if (termsAcceptBtn.isVisible())
                    termsAcceptBtn.click();
                moon = true;
                Element nextBtn = new Element(driver, By.id("com.android.chrome:id/next_button"));
                nextBtn.click();
                try {
                    nextBtn.waitForInvisibility();
                } catch (Exception err) {
                    Log.error(err.getMessage());
                    nextBtn.click();
                }
                //driver.findElement(By.id("com.android.chrome:id/negative_button")).click();
            }
            new WebDriverWait(driver, 30).until(
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
                try {
                    if (redirectBtn.isVisible())
                        redirectBtn.click();
                } catch (Exception err) {
                    Log.error(err.getMessage());
                }
            } else {
                Log.debug("Redirect Button is not visible");
                if (moon) {
                    new ChromeCertificateErrorPage().skipSiteSecureError();
                    androidDriver.context("WEBVIEW_chrome");
                    new LoginWebPage().logIn(userLdap, password);
                    androidDriver.context("NATIVE_APP");
                    try {
                        redirectBtn.click();
                    } catch (NoSuchElementException err) {
                        // Если получили ошибку HTTP ERROR 500
                        androidDriver.context("WEBVIEW_chrome");
                        LoginWebPage loginWebPage = new LoginWebPage();
                        //String consoleErrors = loginWebPage.getJSErrorsFromConsole();
                        //Log.error("CONSOLE ERRORS:" + consoleErrors);
                        loginWebPage.reloadPage();
                        androidDriver.context("NATIVE_APP");
                        try {
                            redirectBtn.click();
                        } catch (Exception err2) {
                            Log.error(err2.getMessage());
                        }
                    }
                } else {
                    if (new Element(driver, By.xpath("//*[@resource-id='Username']")).isVisible(1)) {
                        androidDriver.context("WEBVIEW_chrome");
                        LoginWebPage loginWebPage = new LoginWebPage();
                        loginWebPage.logIn(userLdap, password);
                        androidDriver.context("NATIVE_APP");
                    }
                }
            }

            MainProductAndServicesPage mainProductAndServicesPage;
            try {
                mainProductAndServicesPage = new MainProductAndServicesPage();
            } catch (Exception err) {
                Log.error(err.getMessage());
                Log.debug("Пробуем повторно нажать кнопку 'Войти'");
                new LoginAppPage().clickLoginButton();
                mainProductAndServicesPage = new MainProductAndServicesPage();
            }
            UserProfilePage userProfilePage = null;
            if (selectShopAndDepartment) {
                userProfilePage = setShopAndDepartmentForUser(mainProductAndServicesPage,
                        getUserSessionData().getUserShopId(), getUserSessionData().getUserDepartmentId());
            }

            if (pageClass.equals(MainProductAndServicesPage.class)) {
                if (userProfilePage != null)
                    return (T) userProfilePage.goToSales();
                return (T) mainProductAndServicesPage;
            } else if (pageClass.equals(MainSalesDocumentsPage.class)) {
                if (userProfilePage != null)
                    return (T) userProfilePage.goToSales().goToSalesDocumentsSection();
                return (T) mainProductAndServicesPage.goToSalesDocumentsSection();
            } else if (pageClass.equals(MainCustomerPage.class)) {
                return (T) mainProductAndServicesPage.goToSales()
                        .goToClientsSection();
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
        } catch (Exception err) {
            RetryAnalyzer.enableForceRetry();
            throw err;
        }
    }

    public <T extends BaseAppPage> T loginAndGoTo(Class<T> pageClass) throws Exception {
        return loginAndGoTo(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS,
                false, pageClass);
    }

    public <T extends BaseAppPage> T loginSelectShopAndGoTo(Class<T> pageClass) throws Exception {
        return loginAndGoTo(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS,
                true, pageClass);
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
        MainProductAndServicesPage mainProductAndServicesPage = loginSelectShopAndGoTo(
                MainProductAndServicesPage.class);
        mainProductAndServicesPage.clickSearchBar(false)
                .enterTextInSearchFieldAndSubmit(lmCode);

        new ProductDescriptionPage().clickActionWithProductButton();
        ActionWithProductModalPage modalPage = new ActionWithProductModalPage();
        CartStep1Page basketStep1Page = modalPage.startToCreateSalesDocument()
                .clickAddButton();
        String documentNumber = basketStep1Page.getDocumentNumber();
        basketStep1Page.clickBackButton()
                .returnBack().backToSalesPage();
        return documentNumber;
    }

    @Step("Установить магазин {shop} и отдел {department} для пользователя")
    protected UserProfilePage setShopAndDepartmentForUser(BottomMenuPage page, String shop, String department)
            throws Exception {
        if (department.length() == 1)
            department = "0" + department;
        page.goToMoreSection()
                .goToUserProfile()
                .goToEditShopForm()
                .searchForShopAndSelectById(shop);
        new UserProfilePage()
                .goToEditDepartmentForm()
                .selectDepartmentById(department);
        return new UserProfilePage();
    }

    /**
     * Тест начинается со страницы авторизации, т.е. с нуля?
     */
    protected boolean isStartFromScratch() {
        String ps;
        try {
            ps = getDriver().getPageSource();
        } catch (WebDriverException err) {
            Log.debug(err.getMessage());
            ps = getDriver().getPageSource();
        }
        Element authScreen = new Element(getDriver(), By.xpath("//*[contains(@resource-id, 'auth_constrain_layout')]"));
        return authScreen.isVisible(ps);
    }

}
