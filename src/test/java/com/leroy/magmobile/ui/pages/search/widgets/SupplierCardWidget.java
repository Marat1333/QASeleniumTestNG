package com.leroy.magmobile.ui.pages.search.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.models.SupplierCardData;
import org.openqa.selenium.WebDriver;

public class SupplierCardWidget extends CardWidget<SupplierCardData> {

    public SupplierCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    public Element supplierNameLbl;

    @AppFindBy(xpath = "./android.widget.TextView[2]")
    public Element supplierCodeLbl;

    @AppFindBy(xpath = ".//android.view.ViewGroup")
    public Element checkBoxBtn;

    public static final String SPECIFIC_CHECKBOX_XPATH = "//*[contains(@text, '%s')]/following-sibling::android.view.ViewGroup";

    public String getNumber() {
        return supplierCodeLbl.getText();
    }

    public String getName() {
        return supplierNameLbl.getText();
    }

    @Override
    public SupplierCardData collectDataFromPage(String pageSource) {
        SupplierCardData supplierCardData = new SupplierCardData();
        supplierCardData.setSupplierCode(supplierCodeLbl.getText());
        supplierCardData.setSupplierName(supplierNameLbl.getText());
        return supplierCardData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return supplierNameLbl.isVisible() && checkBoxBtn.isVisible() && supplierCodeLbl.isVisible();
    }
}
