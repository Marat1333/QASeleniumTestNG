package com.leroy.magportal.ui.webelements;

import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;

public class PuzMultiSelectComboBox extends PuzComboBox {

    public PuzMultiSelectComboBox(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    /**
     * Активирует опции в выпадающем списке (если опция уже активирована, то ничего не делает)
     *
     * @param options список опций, которые должны быть активированы
     */
    public void selectOptions(List<String> options) throws Exception {
        clickOptions(options, true, true);
    }

    public void selectOptions(String... values) throws Exception {
        selectOptions(Arrays.asList(values));
    }

    /**
     * Деактивирует (отключает) опции в выпадающем списке (если опция уже отключена, то ничего не делает)
     *
     * @param options список опций, которые должны быть отключены
     */
    public void deselectOptions(List<String> options) throws Exception {
        clickOptions(options, false, true);
    }

    public void deselectOptions(String... values) throws Exception {
        deselectOptions(Arrays.asList(values));
    }

}
