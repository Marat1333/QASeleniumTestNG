package com.leroy.magmobile.ui.pages;

import com.leroy.constants.EnvConstants;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Button;
import com.leroy.models.UserData;
import com.leroy.magmobile.ui.pages.sales.SalesPage;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends BaseAppPage {

    // Main sections of Mag mobile app
    public static final String SALES_SECTION = "Продажа";
    public static final String WORK_SECTION = "Работа";
    public static final String SUPPORT_SECTION = "Поддержка";
    public static final String MORE_SECTION = "еще";

    // Sub sections for the SALES_SECTION
    public static final String DOCUMENTS_SALES_SECTION = "Документы продажи";

    public LoginPage(TestContext context) {
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

    @Step("Зайдите в раздел {section}")
    public void loginInAndGoTo(UserData userData, String section) throws Exception {
        SalesPage salesPage = logIn(userData);
        switch (section) {
            case SALES_SECTION:
                // Nothing to do because it is default page after login
                break;
            case DOCUMENTS_SALES_SECTION:
                salesPage.goToSalesDocumentsSection();
                break;
            case WORK_SECTION:
                salesPage.goToWork();
                break;
            case SUPPORT_SECTION:
                salesPage.goToSupport();
                break;
            case MORE_SECTION:
                salesPage.goToMoreSection();
                break;
        }
    }

    @Step("Зайдите в раздел {section}")
    public void loginInAndGoTo(String section) throws Exception {
        loginInAndGoTo(new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS), section);
    }


    /* ---------------------- Verifications -------------------------- */

}
