package com.leroy.magportal.api.tests;

import com.google.inject.Inject;
import com.leroy.core.ContextProvider;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.ComboBox;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.api.constants.CardConst;
import com.leroy.magportal.api.helpers.PaymentHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

import static org.hamcrest.MatcherAssert.assertThat;

public class BasePaymentTest extends BaseMagPortalApiTest {

    @Inject
    protected PaymentHelper paymentHelper;

    protected void makePayment(String orderId) {
        WebDriver driver = getDriver();
        String link = paymentHelper.getPaymentLink(orderId);
        driver.get(link);

        PaymentPage paymentPage = new PaymentPage();
        paymentPage.enterCreditCardDetails(CardConst.VISA_1111);
        paymentPage.assertThatPaymentIsSuccessful();
    }

    private static class PaymentPage extends BaseWebPage {

        @WebFindBy(id = "iPAN")
        EditBox pan;

        @WebFindBy(id = "month")
        ComboBox month;

        @WebFindBy(id = "year")
        ComboBox year;

        @WebFindBy(id = "iTEXT")
        EditBox text;

        @WebFindBy(id = "iCVC")
        EditBox cvc;

        @WebFindBy(id = "buttonPayment")
        Button buttonPayment;

        @WebFindBy(xpath = "//*[@name='password']")
        EditBox password;

        @WebFindBy(xpath = "//input[@type='submit']")
        Element submitBtn;

        @Override
        protected void waitForPageIsLoaded() {
            try {//sometimes alerts are happen... twice...
                driver.switchTo().alert().accept();
                driver.switchTo().alert().accept();
            } catch (Exception ignored) {
            }
        }

        private WebElement getShadowRootElement(WebDriver driver, WebElement element) {
            return (WebElement) ((JavascriptExecutor) driver)
                    .executeScript("return arguments[0].shadowRoot", element);
        }

        public void enterCreditCardDetails(CardConst.CardData cardData) {
            pan.clearAndFill(cardData.getPan());
            month.selectOptionByText(cardData.getMonth());
            year.selectOptionByText(cardData.getYear());
            text.clearAndFill(cardData.getText());
            cvc.clearAndFill(cardData.getCvc());
            buttonPayment.click();
            password.clearAndFill(cardData.getPassword());
            submitBtn.click();
        }

        public void assertThatPaymentIsSuccessful() {
            Element header = new Element(driver, By.xpath("//transit-header-simple"));
            header.waitForVisibility();
            WebElement basket = getShadowRootElement(driver, header.getWebElement())
                    .findElement(By.cssSelector("uc-container"))
                    .findElement(By.cssSelector("uc-header-basket-old"));

            boolean isBasketDisplayed = basket.isDisplayed();
            assertThat("Payment FAILED because Basket is absent.", isBasketDisplayed);
        }

    }


    // -------------- Web Driver configuration ------------------ //
    @BeforeMethod
    @BeforeClass
    @Parameters({"browser", "platform", "host", "environment", "propsFile",
            "build", "timeout"})
    protected void driverCreation(
            @Optional("") String browser,
            @Optional("") String platform,
            @Optional("") String host,
            @Optional("") String environment,
            @Optional("") String propsFile,
            @Optional("") String build,
            @Optional("") String timeout) throws Exception {
        if (!isConnectionOpen()) {
            createDriver(browser, platform, host, environment, propsFile, build, timeout);
        }
    }

    @AfterMethod
    public void cleanUpAfterMethod() {
        ContextProvider.quitDriver();
    }

    @AfterClass
    protected void cleanUpAfterClass() {
        ContextProvider.quitDriver();
    }

    protected WebDriver getDriver() {
        return ContextProvider.getDriver();
    }

    // Private methods

    private void createDriver(String browser, String platform, String host, String environment,
                              String propsFile, String build, String timeout) throws Exception {
        browser = System.getProperty("mbrowser", browser);
        platform = System.getProperty("mplatform", platform);
        host = System.getProperty(DriverFactory.HOST_ENV_VAR, host);
        //environment = System.getProperty("menv", environment);
        propsFile = System.getProperty("mpropsFile", propsFile);
        build = System.getProperty("mbuild", build);
        timeout = System.getProperty("mtimeout", timeout);

        if (propsFile.isEmpty()) {
            throw new Exception("Property file should be specified");
        }

        ContextProvider.setDriver(
                DriverFactory.createDriver(propsFile, platform, browser, host, timeout, build));
    }

    private boolean isConnectionOpen() {
        RemoteWebDriver driver = (RemoteWebDriver) ContextProvider.getDriver();
        return driver != null && driver.getSessionId() != null;
    }

}
