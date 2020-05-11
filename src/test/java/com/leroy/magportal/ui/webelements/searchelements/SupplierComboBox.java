package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.magportal.ui.webelements.PuzMultiSelectComboBox;
import com.leroy.magportal.ui.webelements.widgets.SupplierCardWidget;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class SupplierComboBox extends PuzMultiSelectComboBox {
    public SupplierComboBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = "./ancestor::div[1]/following-sibling::div[contains(@class, 'Select__menu lmui')]")
    private SupplierDropDown supplierDropDown;

    @Override
    public void selectOptions(List<String> options) throws Exception {
        List<SupplierCardWidget> supplierCards;
        for (String tmp : options) {
            supplierDropDown.searchSupplier(tmp);
            supplierCards = supplierDropDown.getSupplierCards().convertToList();
            for (SupplierCardWidget widget : supplierCards) {
                if (tmp.equals(widget.getSupplierCode()) || tmp.equals(widget.getSupplierName())) {
                    widget.click();
                }
            }
        }
    }
}
