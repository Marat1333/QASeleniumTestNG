package com.leroy.magmobile.ui.pages.widgets;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import org.openqa.selenium.WebDriver;

public class TextViewWidget extends CardWidget<String> {

    public TextViewWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public String collectDataFromPage(String pageSource) {
        return getText(pageSource);
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return isVisible(pageSource);
    }
}
