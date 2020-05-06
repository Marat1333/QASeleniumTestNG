package com.leroy.magportal.ui.webelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;

public class MagPortalComboBox extends BaseWidget {
    public MagPortalComboBox(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    @WebFindBy(xpath = ".//button", metaName = "Кнопка 'v'")
    Element dropBtn;

    @WebFindBy(xpath = ".//span[contains(@class, 'singleValue')]")
    ElementList<Element> chosenStringValue;

    private final String CONTAINER_OPTION_XPATH = "//div[contains(@class, 'optionsContainer')]/div";
    private final String SWITCH_BTN_LABEL_XPATH = CONTAINER_OPTION_XPATH + "//span[contains(@class, 'SwitchButton-label') and text()='%s']";

    @WebFindBy(xpath = "." + CONTAINER_OPTION_XPATH)
    private ElementList<Element> dropDownElementsList;

    /**
     * Получить список выбранных опций в выпадающем списке
     */
    public List<String> getSelectedOptionsText() throws Exception {
        return chosenStringValue.getTextList();
    }

    private void clickOptions(List<String> options, boolean isActivate) throws Exception{
        dropBtn.click();
        dropDownElementsList.waitUntilAtLeastOneElementIsPresent(short_timeout);
        for (String option : options) {
            Element optionElem = E(getXpath() + String.format(
                    SWITCH_BTN_LABEL_XPATH, option));
            if (!optionElem.isVisible())
                throw new IllegalArgumentException(String.format(
                        "Element: %s, selectOptions() - Not found option %s", getMetaName(), option));
            else if (isActivate ^ optionElem.findChildElement(
                    "/../span[contains(@class, 'SwitchButton-icon')]").isPresent()) {
                optionElem.click();
            }
        }
        dropBtn.click();
    }

    /**
     * Активирует опции в выпадающем списке (если опция уже активирована, то ничего не делает)
     * @param options список опций, которые должны быть активированы
     */
    public void selectOptions(List<String> options) throws Exception {
        clickOptions(options, true);
    }

    public void selectOptions(String... values) throws Exception {
        selectOptions(Arrays.asList(values));
    }

    /**
     * Деактивирует (отключает) опции в выпадающем списке (если опция уже отключена, то ничего не делает)
     * @param options список опций, которые должны быть отключены
     */
    public void deselectOptions(List<String> options) throws Exception {
        clickOptions(options, false);
    }

    public void deselectOptions(String... values) throws Exception {
        deselectOptions(Arrays.asList(values));
    }

}
