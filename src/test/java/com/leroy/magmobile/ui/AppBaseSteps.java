package com.leroy.magmobile.ui;

import com.leroy.constants.EnvConstants;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.pages.ChromeCertificateErrorPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.LoginAppPage;
import com.leroy.magmobile.ui.pages.more.MorePage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SalesPage;
import com.leroy.magmobile.ui.pages.support.SupportPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magportal.ui.pages.LoginWebPage;
import com.leroy.models.UserData;
import com.leroy.temp_ui.BaseState;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AppBaseSteps extends BaseState {

    public <T> T loginAndGoTo(UserData userData, Class<? extends BaseAppPage> pageClass) throws Exception {
        AndroidDriver<MobileElement> androidDriver = (AndroidDriver<MobileElement>) driver;
        new LoginAppPage(context).clickLoginButton();
        /// If the Chrome starts first time and we can see pop-up windows
        Element termsAcceptBtn = new Element(driver,
                By.id("com.android.chrome:id/terms_accept"));
        if (termsAcceptBtn.isVisible(5)) {
            termsAcceptBtn.click();
            driver.findElement(By.id("com.android.chrome:id/next_button")).click();
            //driver.findElement(By.id("com.android.chrome:id/negative_button")).click();
        }
        new WebDriverWait(this.driver, 30).until(
                a -> androidDriver.getContextHandles().size() > 1);
        ///
        androidDriver.context("WEBVIEW_chrome");
        // Skip invalid certificates
        new ChromeCertificateErrorPage(context).skipSiteSecureError();
        ///
        new LoginWebPage(context).logIn(userData);
        androidDriver.context("NATIVE_APP");
        androidDriver.findElement(By.xpath("//*[@resource-id='buttonRedirect']")).click();
        SalesPage salesPage = new SalesPage(context);
        if (pageClass.equals(SalesPage.class)) {
            return (T) salesPage;
        } else if (pageClass.equals(SalesDocumentsPage.class)) {
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

}
