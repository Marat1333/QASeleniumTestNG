package com.leroy.core.web_elements.android;

import com.leroy.core.fieldfactory.CustomLocator;
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
    public W getFirstWidget(boolean scrollUpBefore) throws Exception {
        if (scrollUpBefore)
            scrollToBeginning();
        return this.findChildElement(eachRowXpath, rowWidgetClass);
    }

    public W getFirstWidget() throws Exception {
        return getFirstWidget(false);
    }


}
