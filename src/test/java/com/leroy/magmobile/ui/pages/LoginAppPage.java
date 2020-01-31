package com.leroy.magmobile.ui.pages;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magmobile.ui.pages.sales.SalesPage;
import com.leroy.models.UserData;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginAppPage extends BaseAppPage {

    public LoginAppPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "Button")
    public Button loginBtn;

    private SalesPage logIn(UserData loginData) throws Exception {
        loginBtn.click();
        /*Element termsAcceptBtn = new Element(driver,
                By.id("com.android.chrome:id/terms_accept"));
        if (termsAcceptBtn.isVisible(tiny_timeout)) {
            termsAcceptBtn.click();
            driver.findElement(By.id("com.android.chrome:id/next_button")).click();
            driver.findElement(By.id("com.android.chrome:id/negative_button")).click();
        }*/
        AndroidDriver androidDriver = (AndroidDriver) driver;
        new WebDriverWait(this.driver, timeout).until(
                a -> androidDriver.getContextHandles().size() > 1);
        androidDriver.context("WEBVIEW_chrome");
        //new ChromeCertificateErrorPage(context).skipSiteSecureError();
        driver.findElement(By.id("Username")).sendKeys(loginData.getUserName());
        driver.findElement(By.id("Password")).sendKeys(loginData.getPassword());
        try {
            driver.findElement(By.xpath("//*[@value='login']")).click();
        } catch (WebDriverException err) {
            Log.debug(err.getMessage());
        }
        androidDriver.context("NATIVE_APP");

        return new SalesPage(context);
    }

    /* ------------------------- ACTIONS -------------------------- */

    @Step("Нажмите кнопку 'Войти'")
    public void clickLoginButton() throws Exception {
        loginBtn.click();
        E("$AuthScreen__btn_getVersionNumber").waitForInvisibility();
        /*try {
            new WebDriverWait(this.driver, timeout).until(
                    a -> ((AndroidDriver) driver).getContextHandles().size() > 1);
        } catch (TimeoutException err) {
            Log.error(err.getMessage());
        }*/
    }
}
