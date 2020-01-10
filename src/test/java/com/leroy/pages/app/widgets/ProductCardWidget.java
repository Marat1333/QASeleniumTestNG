package com.leroy.pages.app.widgets;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductCardWidget extends Element {

    public ProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public String getNumber() {
        return new Element(driver, By.xpath(getXpath() + "/android.widget.TextView[1]")).getText();
    }

    public String getName() {
        return new Element(driver, By.xpath(getXpath() + "/android.widget.TextView[2]")).getText();
    }

    public String getQuantity() {
        return new Element(driver, By.xpath(getXpath() + "/android.widget.TextView[3]")).getText();
    }

    public String getQuantityType() {
        return new Element(driver, By.xpath(getXpath() + "/android.widget.TextView[4]")).getText();
    }

}
