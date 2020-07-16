package com.leroy.magmobile.ui.pages.work.supply_plan.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierData;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import org.openqa.selenium.WebDriver;

public class SupplierSearchResultWidget extends CardWidget<SupplierData> {

    @AppFindBy(xpath = "./*[1]")
    Element name;

    @AppFindBy(xpath = "./*[2]")
    Element id;

    public SupplierSearchResultWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public SupplierData collectDataFromPage(String pageSource) {
        SupplierData data = new SupplierData();
        data.setSupplierId(id.getText());
        data.setName(name.getText());
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return name.isVisible() && id.isVisible();
    }
}
