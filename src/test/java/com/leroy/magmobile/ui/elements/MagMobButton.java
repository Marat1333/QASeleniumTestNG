package com.leroy.magmobile.ui.elements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

import java.io.IOException;

public class MagMobButton extends Button {

    public MagMobButton(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    private String TEXT_VIEW_XPATH = "//android.widget.TextView";

    @Override
    public String getText() {
        return new Element(driver, By.xpath(getXpath() + TEXT_VIEW_XPATH)).getText();
    }

    @Override
    public String getText(String pageSource) {
        if (pageSource == null)
            return getText();
        String result = getAttributeValueFromPageSource(
                pageSource, "text", getXpath() + TEXT_VIEW_XPATH);
        if (result == null)
            throw new NoSuchElementException(String.format(
                    "Element '%s' with xpath:{%s} wasn't found", getMetaName(), getXpath()));
        return result;
    }

    @Override
    public Color getPointColor() throws IOException {
        return getPointColor(0, 5 - (getHeight() / 2));
    }
}
