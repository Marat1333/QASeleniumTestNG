package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import org.openqa.selenium.WebDriver;

public class CalendarComboBox extends EditBox {
    public CalendarComboBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = "./following-sibling::button[contains(@id,'clearIcon')]")
    Button clearInputBtn;

    public void clearInput() {
        if (clearInputBtn.isVisible()) {
            clearInputBtn.click();
        }
    }
}
