package com.leroy.magportal.ui.pages.products.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class ExtendedProductCardTableViewWidget extends ProductCardTableViewWidget {
    public ExtendedProductCardTableViewWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = "./div[1]//span[contains(@class,'LmCode')]/following-sibling::span")
    Element lmCode;

    @WebFindBy(xpath = "./div[4]/p")
    Element availableStock;

    @WebFindBy(xpath = "./div[4]/span")
    Element avsDate;

    @WebFindBy(xpath = "./div[5]/span")
    Element top;

    @WebFindBy(xpath = "./div[6]/span")
    Element topEm;

    @WebFindBy(xpath = "./div[7]/span")
    Element gamma;

    @WebFindBy(xpath = "./div[8]/p[1]")
    Element supplierName;

    @WebFindBy(xpath = "./div[8]/p[2]")
    Element supplierCode;

    @WebFindBy(xpath = "./div[9]//span")
    Element price;

    public String getAvailableStock() {
        return availableStock.getText().replaceAll("\\D+", "");
    }

    public String getAvsDate() {
        return avsDate.getText().substring(6);
    }

    public String getTop() {
        return top.getText();
    }

    public boolean getTopEm() {
        return topEm.isPresent();
    }

    @Override
    public String getGamma() {
        return gamma.getText();
    }

    @Override
    public String getLmCode() {
        return lmCode.getText();
    }

    public String getSupplierName() {
        return supplierName.getText();
    }

    public String getSupplierCode() {
        return supplierCode.getText();
    }

    public String getPrice() {
        return price.getText();
    }
}
