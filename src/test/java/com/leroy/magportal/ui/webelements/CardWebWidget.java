package com.leroy.magportal.ui.webelements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import org.openqa.selenium.WebDriver;

public abstract class CardWebWidget<T> extends BaseWidget {

    public CardWebWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public abstract T collectDataFromPage() throws Exception;

    public void click() {
        E(this.getXpath()).click();
    }

}
