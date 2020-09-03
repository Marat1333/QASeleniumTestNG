package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class ChosenSupplierWidget extends BaseWidget {

    public ChosenSupplierWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = "./span")
    Element chosenSupplierName;

    @WebFindBy(xpath = "./button")
    Button deleteChosenSupplierBtn;

    public String getChosenSupplierName() {
        return chosenSupplierName.getText();
    }

    public void deleteChosenSupplier() {
        deleteChosenSupplierBtn.click();
        waitForInvisibility(tiny_timeout);
    }
}
