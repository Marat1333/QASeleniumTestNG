package com.leroy.magportal.api.requests.payments;

import static com.leroy.core.ContextProvider.getDriver;

import com.google.inject.Inject;
import com.leroy.core.ContextProvider;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.cart.BaseMagPortalApiTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MakeCardPayment extends BaseMagPortalApiTest {

  private String link;
  private WebDriver driver;

  @Inject
  private PaymentHelper paymentHelper;
//  @Inject
//  private BaseWebPage baseWebPage;

  public MakeCardPayment() throws Exception {
    ContextProvider.setDriver(
        DriverFactory.createDriver(System.getProperty("mpropsFile"),
            "", "", "", "", ""));
    driver = getDriver();
  }

  public void makePayment(String orderId) {

    link = paymentHelper.getPaymentLink(orderId);
    driver.get(link);

    try {//sometimes alerts are happen... twice...
      driver.switchTo().alert().accept();
      driver.switchTo().alert().accept();
    } catch (Exception ignored) { }

    driver.findElement(By.id("iPAN")).sendKeys(CardConst.PAN);
    driver.findElement(By.id("month")).sendKeys(CardConst.MM);
    driver.findElement(By.id("year")).sendKeys(CardConst.YYYY);
    driver.findElement(By.id("iTEXT")).sendKeys(CardConst.TEXT);
    driver.findElement(By.id("iCVC")).sendKeys(CardConst.CVC);

    driver.findElement(By.id("buttonPayment")).click();

    driver.findElement(By.name("password")).sendKeys(CardConst.PASSWORD);
    driver.findElement(By.xpath("//input[@type='submit']")).click();

//    WebElement basket = getShadowRootElement(driver.findElement(By.xpath("//transit-header-simple")))
//        .findElement(By.cssSelector("uc-container"))
//        .findElement(By.cssSelector("uc-header-basket-old"));
//
    boolean isBasketDisplayed = driver.findElement(By.className("uc-container")).isEnabled();
    driver.close();
    Assert.assertTrue(isBasketDisplayed, "Payment FAILED because Basket is absent.");
  }

//  private WebElement getShadowRootElement(WebElement element) {//TODO: below copyPaste. Mb move it public?
//    return (WebElement) ((JavascriptExecutor) driver)
//        .executeScript("return arguments[0].shadowRoot", element);
//  }
}
