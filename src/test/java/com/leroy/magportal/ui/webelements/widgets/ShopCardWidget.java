package com.leroy.magportal.ui.webelements.widgets;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.webelements.commonelements.PriceContainer;
import org.openqa.selenium.WebDriver;

public class ShopCardWidget extends BaseWidget {
    public ShopCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = "./div[1]/span")
    Element shopId;

    @WebFindBy(xpath = "./div[2]/span")
    Element shopName;

    @WebFindBy(xpath = "./div[2]/p[1]")
    Element shopAddress;

    @WebFindBy(xpath = "./div[2]/p[2]")
    Element distance;

    @WebFindBy(xpath = "./div[3]")
    PriceContainer price;

    @WebFindBy(xpath = "./div[3]/p")
    Element availableStock;
}
