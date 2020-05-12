package com.leroy.magportal.ui.webelements.commonelements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Checkbox;
import org.openqa.selenium.WebDriver;

public class PuzCheckBox extends Checkbox {
    public PuzCheckBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public boolean isChecked() throws Exception {
        return this.findChildElement(".//input/preceding-sibling::span[3]").isPresent();
    }
}
