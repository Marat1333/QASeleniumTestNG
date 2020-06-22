package com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;

public class SuppliesPage extends ProductPricesQuantitySupplyPage{

    @AppFindBy(xpath = "//android.widget.ScrollView")
    AndroidScrollView<String> mainScrollView;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Код поставщика')]/preceding-sibling::*")
    Element supplierNameLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Код поставщика')]")
    Element supplierCodeLbl;

    @AppFindBy(xpath = "//*[contains(@text, \"Код поставщика\")]/ancestor::*[2]/*[2]//android.widget.TextView[1]")
    Element supplierPhoneNumberLbl;

    @AppFindBy(xpath = "//*[contains(@text, \"Код поставщика\")]/ancestor::*[2]/*[2]//android.widget.TextView[2]")
    Element supplierContactNameLbl;

    @AppFindBy(xpath = "//*[contains(@text, \"Код поставщика\")]/ancestor::*[2]/*[3]//android.widget.TextView[1]")
    Element supplierEmailLbl;

    @AppFindBy(xpath = "//*[contains(@text, \"Плановая дата\")]/following-sibling::*")
    Element todayOrderSupplyDateLbl;

    @AppFindBy(xpath = "//*[contains(@text, \"Дата ближайшего заказа\")]/following-sibling::*")
    Element nearestSupplyDateLbl;

    public SuppliesPage verifyRequiredElements(){
        return this;
    }

    @Override
    public void waitForPageIsLoaded() {
        waitUntilProgressBarIsVisible();
        waitUntilProgressBarIsInvisible();
    }
}
