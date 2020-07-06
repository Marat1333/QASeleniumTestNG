package com.leroy.magportal.ui.webelements.commonelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import org.openqa.selenium.WebDriver;

import java.util.Collections;
import java.util.List;

public class PuzComboBox extends Element {

    public PuzComboBox(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    @WebFindBy(xpath = ".//input")
    EditBox input;

    @WebFindBy(xpath = ".//button", metaName = "Кнопка 'v'")
    protected Element dropBtn;

    @WebFindBy(xpath = ".//div[contains(@class, 'Select__valueContainer')]")
    protected Element selectedStringValue;

    private final String CONTAINER_OPTION_XPATH = "//div[contains(@class, 'optionsContainer')]/div";
    private final String SWITCH_BTN_LABEL_XPATH = CONTAINER_OPTION_XPATH + "//span[contains(@class, 'SwitchButton-label') and text()='%s']";

    @WebFindBy(xpath = CONTAINER_OPTION_XPATH + "//span[contains(@class, 'label')]")
    protected ElementList<Element> dropDownElementsList;

    public boolean isEnabled() {
        return input.isEnabled();
    }

    protected void clickOptions(List<String> options, boolean isActivate, boolean closeAfter) throws Exception {
        open();
        dropDownElementsList.waitUntilAtLeastOneElementIsPresent(short_timeout);
        for (String option : options) {
            Element optionElem = E(String.format(
                    SWITCH_BTN_LABEL_XPATH, option));
            if (!optionElem.isVisible())
                throw new IllegalArgumentException(String.format(
                        "Element: %s, selectOptions() - Not found option %s", getMetaName(), option));
            else if (isActivate ^ optionElem.findChildElement(
                    "/../span[contains(@class, 'SwitchButton-icon')]").isPresent()) {
                optionElem.click();
            }
        }
        if (closeAfter)
            close();
    }

    protected void open() {
        if (!E(CONTAINER_OPTION_XPATH).isVisible())
            dropBtn.click();
    }

    protected void close() {
        if (E(CONTAINER_OPTION_XPATH).isVisible())
            dropBtn.click();
    }

    /**
     * Получить список выбранных опций в выпадающем списке
     */
    public String getSelectedOptionText() {
        return selectedStringValue.getText();
    }

    /**
     * Получить список опций в выпадающем списке
     */
    public List<String> getOptionList() throws Exception {
        open();
        List<String> result = dropDownElementsList.getTextList();
        close();
        return result;
    }

    /**
     * Активирует опцию в выпадающем списке (если опция уже активирована, то ничего не делает)
     *
     * @param option опция, которая должна быть активирована
     */
    public void selectOption(String option) throws Exception {
        clickOptions(Collections.singletonList(option), true, false);
    }

}

