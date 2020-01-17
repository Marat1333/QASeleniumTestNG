package com.leroy.magmobile.ui.pages.widgets;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.models.TextViewData;
import org.openqa.selenium.WebDriver;

public class TextViewWidget extends CardWidget<TextViewData> {

    public TextViewWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public TextViewData collectDataFromPage() {
        return new TextViewData(getText());
    }

    @Override
    public boolean isFullyVisible() {
        return isVisible();
    }
}
