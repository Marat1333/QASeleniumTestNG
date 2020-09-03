package com.leroy.core.web_elements.android;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AndroidScrollViewV2<W extends CardWidget<D>, D> extends AndroidScrollView<D> {

    private Class<W> rowWidgetClass;

    public AndroidScrollViewV2(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public AndroidScrollViewV2(WebDriver driver, By by) {
        super(driver, by);
    }

    public AndroidScrollViewV2(WebDriver driver, By by, String eachRowXpath, Class<W> clazz) {
        super(driver, by, eachRowXpath, clazz);
        rowWidgetClass = clazz;
    }

    public AndroidScrollViewV2(WebDriver driver, CustomLocator locator, String eachRowXpath, Class<W> clazz) {
        super(driver, locator, eachRowXpath, clazz);
        rowWidgetClass = clazz;
    }

    /**
     * Получить первый widget
     */
    public W getWidget(int index, boolean scrollUpBefore) throws Exception {
        if (scrollUpBefore)
            scrollToBeginning();
        String xpath = eachRowXpath.startsWith(".")? eachRowXpath.replaceFirst("\\.", "") : eachRowXpath;
        ElementList<W> widgets = EL(xpath, null, rowWidgetClass);
        return widgets.get(index);
    }

    public W getWidget(int index) throws Exception {
        return getWidget(index, false);
    }


}
