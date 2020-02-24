package com.leroy.magmobile.ui.pages.common.widget;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.models.CardWidgetData;
import org.openqa.selenium.WebDriver;

public abstract class CardWidget<T extends CardWidgetData> extends Element {

    public CardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public T collectDataFromPage() {
        return collectDataFromPage(null);
    }

    public abstract T collectDataFromPage(String pageSource);

    public boolean isFullyVisible() {
        return isFullyVisible(null);
    }

    public abstract boolean isFullyVisible(String pageSource);

}
