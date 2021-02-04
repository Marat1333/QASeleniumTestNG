package com.leroy.magportal.api.helpers.ui;

import static org.hamcrest.MatcherAssert.assertThat;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.api.constants.CardConst;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PaymentPage extends BaseWebPage {

    @WebFindBy(id = "cardNumber")
    EditBox pan;

    @WebFindBy(xpath = "//*[@name='date']")
    EditBox date;

//    @WebFindBy(id = "month")
//    ComboBox month;
//
//    @WebFindBy(id = "year")
//    ComboBox year;

    @WebFindBy(id = "iTEXT")
    EditBox text;

    @WebFindBy(id = "iCVC")
    EditBox cvc;

    @WebFindBy(id = "buttonPayment")
    Button buttonPayment;

    @WebFindBy(xpath = "//*[@name='password']")
    EditBox password;

    @WebFindBy(xpath = "//input[@id='submit_id']")
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
        date.clearAndFill(cardData.getMonth() + cardData.getYear());
//        month.selectOptionByText(cardData.getMonth());
//        year.selectOptionByText(cardData.getYear());
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
