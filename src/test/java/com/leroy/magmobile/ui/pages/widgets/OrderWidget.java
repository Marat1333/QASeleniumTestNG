package com.leroy.magmobile.ui.pages.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class OrderWidget extends Element {

    public OrderWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    public Element numberLbl;

    @AppFindBy(xpath = "./android.widget.TextView[3]")
    public Element dateLbl;

    @AppFindBy(xpath = "./android.view.ViewGroup/android.widget.TextView")
    public Element typeLbl;

}
