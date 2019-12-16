package com.leroy.pages.app.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class OrderWidget extends Element {

    public OrderWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public OrderWidget(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    public OrderWidget(WebDriver driver, WebElement we, CustomLocator locator) {
        super(driver, we, locator);
    }

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    public Element numberLbl;

    @AppFindBy(xpath = "./android.widget.TextView[3]")
    public Element dateLbl;

    @AppFindBy(xpath = "./android.view.ViewGroup/android.widget.TextView")
    public Element typeLbl;

}
