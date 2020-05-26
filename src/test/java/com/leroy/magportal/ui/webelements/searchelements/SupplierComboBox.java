package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.webelements.commonelements.PuzMultiSelectComboBox;
import org.openqa.selenium.WebDriver;

public class SupplierComboBox extends PuzMultiSelectComboBox {

    public SupplierComboBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//div[contains(@class, 'Select__optionsContainer')]")
    Element container;

    @WebFindBy(xpath = ".//span[contains(@class,'single')]")
    Element chosenSupplierName;

    @WebFindBy(xpath = ".//input[@placeholder='Поиск']")
    EditBox searchString;

    @WebFindBy(xpath = ".//input[@placeholder='Поиск']/following-sibling::button")
    Button clearTextInputBtn;

    @WebFindBy(xpath = ".//span[text()='Очистить']/ancestor::button")
    Button clearBtn;

    @WebFindBy(xpath = "./div[contains(@class, 'options')]/div/div[3]/div/span")
    Element departmentName;

    @WebFindBy(xpath = ".//div[contains(@class, 'SuppliersMenuComponents__option')]",
            clazz = SupplierCardWidget.class)
    public ElementList<SupplierCardWidget> supplierCards;

    @WebFindBy(xpath = ".//div[contains(@class,'SuppliersMenuComponents__chip')]",
            clazz = ChosenSupplierWidget.class)
    private ElementList<ChosenSupplierWidget> chosenSuppliers;

    @WebFindBy(xpath = ".//div[contains(@class, 'Spinner-active')]")
    Element loadingSpinner;


    public void searchSupplier(String value) {
        open();
        if (!searchString.getText().isEmpty()) {
            clearTextInputBtn.click();
        }
        searchString.fill(value);
        loadingSpinner.waitForVisibility(tiny_timeout);
        loadingSpinner.waitForInvisibility();
    }

    public void searchSupplierAndSelect(String value) {
        searchSupplier(value);
        for (SupplierCardWidget widget : supplierCards) {
            if (widget.getSupplierCode().equals(value) || widget.getSupplierName().equals(value)) {
                widget.click();
            }
        }
    }

    public Boolean isSupplierSelected(String supplier) throws Exception {
        for (SupplierCardWidget widget : supplierCards) {
            if (widget.getSupplierCode().contains(supplier) || widget.getSupplierName().contains(supplier)) {
                return widget.isSelected();
            }
        }
        return null;
    }

    public void deleteAllChosenSuppliers() {
        open();
        clearBtn.click(short_timeout);
        chosenSuppliers.waitUntilElementCountEquals(0);
    }

    public void deleteChosenSuppliers(String... values) {
        open();
        for (ChosenSupplierWidget widget : chosenSuppliers) {
            String currentSupplier = widget.getChosenSupplierName();
            for (String value : values) {
                if (currentSupplier.equals(value)) {
                    widget.deleteChosenSupplier();
                    break;
                }
            }
        }
    }

    public void open() {
        if (!container.isVisible()) {
            click();
            loadingSpinner.waitForInvisibility();
        }
    }

    public void close() {
        if (container.isVisible())
            click();
    }

}
