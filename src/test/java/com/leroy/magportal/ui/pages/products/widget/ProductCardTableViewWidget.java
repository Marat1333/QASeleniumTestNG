package com.leroy.magportal.ui.pages.products.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class ProductCardTableViewWidget extends Element {

    public ProductCardTableViewWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = "./div[1]//span[contains(@class,'TableView')]/following-sibling::span")
    Element lmCode;

    @WebFindBy(xpath = "./div[2]/span")
    Element barCode;

    @WebFindBy(xpath = "./div[3]/span")
    Element title;

    @WebFindBy(xpath = "./div[4]/span")
    private Element gamma;

    public String getLmCode() {
        return lmCode.getText();
    }

    public String getBarCode() {
        return barCode.getText();
    }

    public String getTitle() {
        return title.getText();
    }

    public String getGamma() {
        return gamma.getText();
    }

}
