package com.leroy.magportal.ui.pages.products.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class ProductCardWidget extends Element {
    public ProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//span[contains(@class, 'LmCode')]/following-sibling::span")
    protected Element lmCodeLbl;

    @WebFindBy(xpath = ".//span[contains(@class, 'Text-pl')]")
    protected Element barCodeLbl;

    @WebFindBy(xpath = ".//p[contains(@class, 'title')]")
    protected Element titleLbl;

    public String getLmCode(){
        return lmCodeLbl.getText();
    }

    public String getBarCode(){
        return barCodeLbl.getText().replaceAll(" ","");
    }

    public String getTitle(){
        return titleLbl.getText();
    }
}
