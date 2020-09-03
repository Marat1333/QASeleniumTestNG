package com.leroy.magportal.ui.pages.products.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.webelements.commonelements.PriceContainer;
import org.openqa.selenium.WebDriver;

public class ExtendedProductCardWidget extends ProductCardWidget {
    public ExtendedProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//span[contains(@class, 'Price-container')]/ancestor::div[1]")
    PriceContainer price;

    @WebFindBy(xpath = "./div[3]/div[1]/span")
    Element availableQuantity;

    public String getPrice() {
        return price.getText();
    }

    public String getAvailableQuantity() {
        return availableQuantity.getText();
    }
}
