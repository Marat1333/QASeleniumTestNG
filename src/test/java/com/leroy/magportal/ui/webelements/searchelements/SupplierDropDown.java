package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.*;
import com.leroy.magportal.ui.webelements.widgets.ChosenSupplierWidget;
import com.leroy.magportal.ui.webelements.widgets.SupplierCardWidget;
import org.openqa.selenium.WebDriver;

public class SupplierDropDown extends BaseWidget {
    public SupplierDropDown(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//div[contains(@class,'inputContainer')]/input")
    EditBox searchString;

    @WebFindBy(xpath = ".//span[text()='Очистить']/ancestor::button")
    Button clearBtn;

    @WebFindBy(xpath = "./div[contains(@class, 'options')]/div/div[3]/div/span")
    Element departmentName;

    @WebFindBy(xpath = "./div[contains(@class, 'options')]/div/div[3]/div/div/div", clazz = SupplierCardWidget.class)
    private ElementList<SupplierCardWidget> supplierCards;

    @WebFindBy(xpath = ".//div[contains(@class,'SuppliersMenuComponents__chip')]", clazz = ChosenSupplierWidget.class)
    private ElementList<ChosenSupplierWidget> chosenSuppliers;

    @WebFindBy(xpath = ".//div[contains(@class, 'Spinner-active')]")
    public Element loadingSpinner;

    public void searchSupplier(String value) {
        searchString.clearAndFill(value, true);
        loadingSpinner.waitForInvisibility(short_timeout);
    }

    public ElementList<SupplierCardWidget> getSupplierCards() {
        return supplierCards;
    }

    public ElementList<ChosenSupplierWidget> getChosenSuppliers() {
        return chosenSuppliers;
    }
}
