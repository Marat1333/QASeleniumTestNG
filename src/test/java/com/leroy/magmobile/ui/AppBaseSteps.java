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
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;

public class AppBaseSteps extends MagMobileBaseTest {

    @BeforeClass
    public void appBaseStepsBeforeClass() {
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId(EnvConstants.SHOP_WITH_NEW_INTERFACE);
        sessionData.setUserDepartmentId("1");
    }

    public <T> T loginAndGoTo(String userLdap, String password, boolean selectShopAndDepartment,
                              Class<? extends BaseAppPage> pageClass) throws Exception {
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
                new LoginWebPage(context).logIn(userLdap, password);
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
                    loginWebPage.logIn(userLdap, password);
                    androidDriver.context("NATIVE_APP");
                }
            }
        }

        MainProductAndServicesPage mainProductAndServicesPage = new MainProductAndServicesPage(context);
        UserProfilePage userProfilePage = null;
        if (selectShopAndDepartment) {
            userProfilePage = setShopAndDepartmentForUser(mainProductAndServicesPage,
                    context.getSessionData().getUserShopId(), context.getSessionData().getUserDepartmentId());
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
        return loginAndGoTo(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS,
                false, pageClass);
    }

    public <T> T loginSelectShopAndGoTo(Class<? extends BaseAppPage> pageClass) throws Exception {
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
        if (department.length() == 1)
            department = "0" + department;
        return page.goToMoreSection()
                .goToUserProfile()
                .goToEditShopForm()
                .searchForShopAndSelectById(shop)
                .goToEditDepartmentForm()
                .selectDepartmentById(department);
    }

}
