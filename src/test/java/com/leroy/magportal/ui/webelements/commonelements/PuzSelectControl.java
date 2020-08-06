package com.leroy.magportal.ui.webelements.commonelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import org.openqa.selenium.WebDriver;

/**
 * Выпадающий список с разными опциями (опции НЕ радио кнопки или чек-боксы)
 */
public class PuzSelectControl extends PuzComboBox {

    public PuzSelectControl(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    @WebFindBy(xpath = "//div[contains(@id, 'react-select')]")
    ElementList<Element> options;

    /**
     * Активирует опцию в выпадающем списке (если опция уже активирована, то ничего не делает)
     *
     * @param option опция, которая должна быть активирована
     */
    @Override
    public void selectOption(String option) throws Exception {
        click();
        options.findElemByText(option, true).click();
    }

}
