package com.leroy.magmobile.ui.pages.widgets;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SelectedCardWidget extends ProductCardWidget {

    public SelectedCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public String getName() {
        return new Element(driver, By.xpath(getXpath() + "//android.widget.TextView[4]")).getText();
    }

    public String getSelectedQuantity() {
        return new Element(driver, By.xpath(getXpath() + "//android.widget.TextView[2]")).getText();
    }

}
