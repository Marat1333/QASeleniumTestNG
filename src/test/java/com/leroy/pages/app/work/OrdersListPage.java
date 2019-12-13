package com.leroy.pages.app.work;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.pages.app.widgets.OrderWidget;
import org.openqa.selenium.WebDriver;

public class OrdersListPage extends BaseAppPage {

    public OrdersListPage(WebDriver driver) {
        super(driver);
    }

    @AppFindBy(accessibilityId = "ScreenTitle")
    public Element titleLbl;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[contains(@text, 'IP')]]",
            clazz = OrderWidget.class)
    public ElementList<OrderWidget> orderList;

    @Override
    public void waitForPageIsLoaded() {
        titleLbl.waitForVisibility();
        titleLbl.waitUntilTextIsEqualTo("Отзыв с RM");
    }
}
