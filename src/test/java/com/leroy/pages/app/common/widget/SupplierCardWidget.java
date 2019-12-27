package com.leroy.pages.app.common.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SupplierCardWidget extends Element {

    public SupplierCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public SupplierCardWidget(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    public SupplierCardWidget(WebDriver driver, WebElement we, CustomLocator locator) {
        super(driver, we, locator);
    }

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    public Element supplierNameLbl;

    @AppFindBy(xpath = "./android.widget.TextView[2]")
    public Element supplierCodeLbl;

    @AppFindBy(xpath = "./android.view.ViewGroup")
    public Element checkBoxBtn;

    public static final String SPECIFIC_CHECKBOX_XPATH = "//*[contains(@text, '%s')]/following-sibling::android.view.ViewGroup";
    public String getNumber() {
        return supplierCodeLbl.getText();
    }

    public String getName() {
        return supplierNameLbl.getText().replaceAll("\\D+","");
    }
}
