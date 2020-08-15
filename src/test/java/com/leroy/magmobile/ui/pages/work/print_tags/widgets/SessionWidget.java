package com.leroy.magmobile.ui.pages.work.print_tags.widgets;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.print_tags.data.SessionData;
import org.openqa.selenium.WebDriver;

public class SessionWidget extends CardWidget<SessionData> {
    public SessionWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public SessionData collectDataFromPage(String pageSource) {
        return null;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return false;
    }
}
