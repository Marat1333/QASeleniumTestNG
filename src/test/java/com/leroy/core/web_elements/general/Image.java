package com.leroy.core.web_elements.general;

import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.WebDriver;

public class Image extends Element{
    public Image(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public String getLink(){
        return this.getAttribute("currentSrc");
    }

}
