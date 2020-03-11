package com.leroy.magportal.ui.webelements.commonelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.NoSuchElementException;
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

    public String getIntegerPrice() {
        return priceIntegerPart.getText().replaceAll("\\D+", "");
    }

    public String getDecimalPrice() {
        String tmp = priceIntegerPart.getText().replaceAll("\\D+", "");
        try {
            return tmp + priceDecimalPart.getText();
        } catch (NoSuchElementException e) {
            Log.error("Price haven`t got decimal part");
            return tmp;
        }
    }

    public String getPriceCurrency() {
        return priceCurrency.getText();
    }

    public String getUnit() {
        try {
            return pricePerUnit.getText();
        } catch (NoSuchElementException e) {
            Log.error("There is no price unit");
            return null;
        }
    }

}
