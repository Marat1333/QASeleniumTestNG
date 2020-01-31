package com.leroy.magmobile.ui.pages.widgets;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.models.TextViewData;
import org.openqa.selenium.WebDriver;

public class TextViewWidget extends CardWidget<TextViewData> {

    public TextViewWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public TextViewData collectDataFromPage(String pageSource) {
        return new TextViewData(getText(pageSource));
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return isVisible(pageSource);
    }

    @Override
    public boolean isServiceCard() {
        return isVisible();
    }
}
