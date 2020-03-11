package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.webelements.MagPortalComboBox;
import com.leroy.magportal.ui.webelements.widgets.SupplierCardWidget;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class SupplierComboBox extends MagPortalComboBox {
    public SupplierComboBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = "./ancestor::div[1]/following-sibling::div[contains(@class, 'Select__menu lmui')]")
    SupplierDropDown supplierDropDown;

    @Override
    public void pickElementFromList(List<String> elements) {
        ElementList<SupplierCardWidget> supplierCards;
        for (String tmp : elements) {
            supplierDropDown.searchSupplier(tmp);
            supplierCards = supplierDropDown.supplierCards;
            for (SupplierCardWidget widget : supplierCards) {
                if (tmp.equals(widget.getSupplierCode()) || tmp.equals(widget.getSupplierName())) {
                    widget.click();
                }
            }
        }
    }
}
