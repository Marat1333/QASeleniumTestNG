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
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SalesPage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep1Page;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.ActionWithProductModalPage;
import com.leroy.magmobile.ui.pages.support.SupportPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magportal.ui.pages.LoginWebPage;
import com.leroy.models.UserData;
import com.leroy.temp_ui.BaseState;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AppBaseSteps extends BaseState {

    public <T> T loginAndGoTo(UserData userData, Class<? extends BaseAppPage> pageClass) throws Exception {
        AndroidDriver<MobileElement> androidDriver = (AndroidDriver<MobileElement>) driver;
        Element redirectBtn = new Element(driver, By.xpath("//*[@resource-id='buttonRedirect']"));
        new LoginAppPage(context).clickLoginButton();
        /// If the Chrome starts first time and we can see pop-up windows
        boolean moon = false;
        Element termsAcceptBtn = new Element(driver,
                By.id("com.android.chrome:id/terms_accept"));
        if (termsAcceptBtn.isVisible()) {
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
                androidDriver.context("WEBVIEW_chrome");
                new ChromeCertificateErrorPage(context).skipSiteSecureError();
                new LoginWebPage(context).logIn(userData);
                androidDriver.context("NATIVE_APP");
            } else {
                if (new Element(driver, By.xpath("//*[@resource-id='Username']")).isVisible(1)) {
                    androidDriver.context("WEBVIEW_chrome");
                    LoginWebPage loginWebPage = new LoginWebPage(context);
                    loginWebPage.logIn(userData);
                    androidDriver.context("NATIVE_APP");
                }
            }
        }

        SalesPage salesPage = new SalesPage(context);
        if (pageClass.equals(SalesPage.class)) {
            return (T) salesPage;
        } else if (pageClass.equals(MainSalesDocumentsPage.class)) {
            return (T) salesPage.goToSalesDocumentsSection();
        } else if (pageClass.equals(WorkPage.class)) {
            return (T) salesPage.goToWork();
        } else if (pageClass.equals(SupportPage.class)) {
            return (T) salesPage.goToSupport();
        } else if (pageClass.equals(MorePage.class)) {
            return (T) salesPage.goToMoreSection();
        } else {
            throw new IllegalArgumentException("Переход на страницу " + pageClass.getName() +
                    " еще не реализован через класс TopMenuPage");
        }
    }

    public <T> T loginAndGoTo(Class<? extends BaseAppPage> pageClass) throws Exception {
        return loginAndGoTo(new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS), pageClass);
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
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        salesPage.clickSearchBar(false)
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
