package com.leroy.magportal.ui.webelements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public abstract class CardWebWidget<T> extends Element {

    public CardWebWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public abstract T collectDataFromPage();

}
