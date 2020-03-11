package com.leroy.magportal.ui.webelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class MagPortalComboBox extends Element {
    public MagPortalComboBox(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    @WebFindBy(xpath = ".//span[contains(@class, 'singleValue')]")
    Element chosenStringValue;

    @WebFindBy(xpath = "./ancestor::div[3]/div[@class='lmui-View lmui-Select lmui-Select__menu']//div[contains(@class, 'optionsContainer')]/div")
    private ElementList<Element> dropDownElementsList;

    public String getChosenValue() {
        return chosenStringValue.getText();
    }

    public void pickElementFromList(List<String> elements) throws Exception {
        for (String element : elements) {
            for (Element tmp : dropDownElementsList) {
                if (tmp.findChildElement("//span[contains(@class, 'SwitchButton-label')]").getText().equals(element)) {
                    tmp.click();
                }
            }
        }
    }

}
