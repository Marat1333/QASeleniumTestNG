package com.leroy.pages.app.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SearchProductCardWidget extends Element {

    public SearchProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public SearchProductCardWidget(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    public SearchProductCardWidget(WebDriver driver, WebElement we, CustomLocator locator) {
        super(driver, we, locator);
    }

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    private Element numberObj;

    @AppFindBy(xpath = "./android.widget.TextView[2]")
    private Element barCodeObj;

    @AppFindBy(xpath = "./android.widget.TextView[3]")
    private Element nameObj;

    @AppFindBy(xpath = "./android.widget.TextView[4]")
    private Element priceObj;

    @AppFindBy(xpath = "./android.widget.TextView[5]")
    private Element priceLbl;

    @AppFindBy(xpath = "./android.widget.TextView[6]")
    private Element quantityObj;

    @AppFindBy(xpath = "./android.widget.TextView[7]")
    private Element quantityType;

    @AppFindBy(xpath = "./android.widget.TextView[8]")
    private Element quantityLbl;

    public String getNumber() {
        return numberObj.getText().replaceAll("^\\D+","");
    }

    public String getBarCode() {
        return barCodeObj.getText().replaceAll(" ","");
    }

    public String getName() {
        return nameObj.getText();
    }

    public String getPrice() {
        return priceObj.getText();
    }

    public String getPriceLbl() {
        return priceLbl.getText();
    }

    public String getQuantity() {
        return quantityObj.getText();
    }

    public String getQuantityType() {
        return quantityType.getText();
    }

    public String getQuantityLbl() {
        return quantityLbl.getText();
    }

}
