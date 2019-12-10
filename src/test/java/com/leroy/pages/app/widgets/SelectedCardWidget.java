package com.leroy.pages.app.widgets;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SelectedCardWidget extends ProductCardWidget {

    public SelectedCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public SelectedCardWidget(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    public SelectedCardWidget(WebDriver driver, WebElement we, CustomLocator locator) {
        super(driver, we, locator);
    }

    @Override
    public String getName() {
        return new Element(driver, By.xpath(getXpath() + "/android.widget.TextView[4]")).getText();
    }

    public String getQuantity() {
        return new Element(driver, By.xpath(getXpath() + "/android.widget.TextView[2]")).getText();
    }

}
