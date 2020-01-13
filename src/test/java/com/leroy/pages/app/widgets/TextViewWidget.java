package com.leroy.pages.app.widgets;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
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
}
