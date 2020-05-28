package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class ProductQuantityInfoWidget extends BaseWidget {
    public ProductQuantityInfoWidget(WebDriver driver) {
        super(driver);
    }

    @WebFindBy(xpath = "//span[contains(text(),'Доступно для продажи')]/../following-sibling::*/span")
    Element availableForSaleLbl;

}
