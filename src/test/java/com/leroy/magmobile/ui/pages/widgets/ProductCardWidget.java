package com.leroy.magmobile.ui.pages.widgets;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductCardWidget extends Element {

    public ProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public String getNumber() {
        return new Element(driver, By.xpath(getXpath() + "//android.widget.TextView[1]")).getText();
    }

    public String getName() {
        return new Element(driver, By.xpath(getXpath() + "//android.widget.TextView[2]")).getText();
    }

    public String getQuantity() {
        return new Element(driver, By.xpath(getXpath() + "//android.widget.TextView[@content-desc='presenceValue'][last()]")).getText();
    }

    public String getQuantityUnit() {
        return new Element(driver, By.xpath(getXpath() + "//android.widget.TextView[@content-desc='priceUnit']")).getText();
    }

}
