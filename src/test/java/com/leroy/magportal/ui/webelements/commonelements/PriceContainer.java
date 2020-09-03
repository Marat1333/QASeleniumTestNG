package com.leroy.magportal.ui.webelements.commonelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class PriceContainer extends Element {

    public PriceContainer(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = "./span[contains(@class,'Price')]")
    private Element priceIntegerPart;

    @WebFindBy(xpath = ".//span[contains(@class,'Price-decimal')]")
    private Element priceDecimalPart;

    @WebFindBy(xpath = ".//span[contains(@class,'currency')]")
    private Element priceCurrency;

    @WebFindBy(xpath = "./span[2]")
    private Element pricePerUnit;

    private String getIntegerPrice() {
        return priceIntegerPart.isVisible() ? priceIntegerPart.getText().split(",")[0] : null;
    }

    public String getDecimalPrice() {
        String tmp = ParserUtil.strWithOnlyDigits(getIntegerPrice());
        if (priceDecimalPart.isVisible()) {
            return tmp + "," + priceDecimalPart.getText();
        } else {
            return tmp;
        }
    }

    public String getPriceCurrency() {
        return priceCurrency.isVisible() ? priceCurrency.getText() : null;
    }

    public String getUnit() {
        String val = pricePerUnit.getTextIfPresent();
        if (val == null) {
            return null;
        }
        return val.replaceAll("/", "");
    }

}
