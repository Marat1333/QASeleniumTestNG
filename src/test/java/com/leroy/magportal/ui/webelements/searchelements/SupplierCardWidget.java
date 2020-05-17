package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.webelements.commonelements.PuzCheckBox;
import org.openqa.selenium.WebDriver;

public class SupplierCardWidget extends Element {
    public SupplierCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//button")
    PuzCheckBox checkbox;

    @WebFindBy(xpath = "./div/span")
    Element supplierName;

    @WebFindBy(xpath = "./div/div/span[2]")
    Element supplierCode;

    public String getSupplierName() {
        return supplierName.getText();
    }

    public String getSupplierCode() {
        return supplierCode.getText();
    }

    public boolean isSelected() throws Exception {
        return checkbox.isChecked();
    }
}
