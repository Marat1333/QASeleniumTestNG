package com.leroy.pages.app.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.pages.app.widgets.OrderWidget;
import io.qameta.allure.Step;

public class OrdersListPage extends BaseAppPage {

    public OrdersListPage(TestContext context) {
        super(context);
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

    @Step("Открыть {index}-ую заявку")
    public OrderDetailsPage clickOrderByIndex(int index) throws Exception {
        orderList.get(index).click();
        return new OrderDetailsPage(context);
    }

    public OrdersListPage shouldOrderByIndexIs(
            int index, String number, String date, String type) throws Exception {
        OrderWidget orderWidget = orderList.get(index);
        softAssert.isEquals(orderWidget.numberLbl.getText(), number,
                "Номер " + index + "-ой заявки должен быть %s");
        softAssert.isEquals(orderWidget.dateLbl.getText(), date,
                "Дата " + index + "-ой заявки должен быть %s");
        softAssert.isEquals(orderWidget.typeLbl.getText(), type,
                "Статус " + index + "-ой завяки должен быть %s");
        softAssert.verifyAll();
        return this;
    }

}
