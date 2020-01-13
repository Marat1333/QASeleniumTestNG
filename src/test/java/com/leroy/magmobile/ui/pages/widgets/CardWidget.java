package com.leroy.magmobile.ui.pages.widgets;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.models.CardWidgetData;
import org.openqa.selenium.WebDriver;

public abstract class CardWidget<T extends CardWidgetData> extends Element {

    public CardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public abstract T collectDataFromPage();

}
