package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.widgets.OrderWidget;
import io.qameta.allure.Step;

public class OrdersListPage extends CommonMagMobilePage {

    public OrdersListPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "ScreenTitle")
    private Element titleLbl;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[contains(@text, 'IP')]]",
            clazz = OrderWidget.class)
    private ElementList<OrderWidget> orderList;

    @Override
    public void waitForPageIsLoaded() {
        titleLbl.waitForVisibility();
        titleLbl.waitUntilTextIsEqualTo("Отзыв с RM");
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Открыть {index}-ую заявку")
    public OrderDetailsPage clickOrderByIndex(int index) throws Exception {
        orderList.get(index).click();
        return new OrderDetailsPage(context);
    }

    /* -------------------------  Verifications -------------------------- */

    @Step("Проверить, что {index}-ая заявка должна иметь номер {number}, Дата - {date}, Статус - {type}")
    public OrdersListPage shouldOrderByIndexIs(
            int index, String number, String date, String type) throws Exception {
        OrderWidget orderWidget = orderList.get(index-1);
        softAssert.isEquals(orderWidget.numberLbl.getText(), number,
                "Номер " + index + "-ой заявки должен быть %s");
        if (date != null) {
            softAssert.isEquals(orderWidget.dateLbl.getText(), date,
                    "Дата " + index + "-ой заявки должен быть %s");
        }
        softAssert.isEquals(orderWidget.typeLbl.getText(), type,
                "Статус " + index + "-ой завяки должен быть %s");
        softAssert.verifyAll();
        return this;
    }

}
