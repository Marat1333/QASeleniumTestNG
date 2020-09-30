package com.leroy.magmobile.ui.pages.common.widget;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public abstract class CardWidget<T> extends Element {

    public CardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public T collectDataFromPage() {
        return collectDataFromPage(getPageSource());
    }

    public abstract T collectDataFromPage(String pageSource);

    /**
     * Собирает только короткий набор данных
     */
    public T collectShortDataFromPage(String pageSource) {
        return collectDataFromPage(pageSource);
    }

    public boolean isFullyVisible() {
        return isFullyVisible(null);
    }

    public abstract boolean isFullyVisible(String pageSource);

}
