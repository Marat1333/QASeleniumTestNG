package com.leroy.magmobile.ui.pages.work.recall_from_rm;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.widgets.OrderWidget;
import io.qameta.allure.Step;

public class OrdersListPage extends CommonMagMobilePage {

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
        return new OrderDetailsPage();
    }

    /* -------------------------  Verifications -------------------------- */

    @Step("Проверить, что {index}-ая заявка должна иметь номер {number}, Дата - {date}, Статус - {type}")
    public OrdersListPage shouldOrderByIndexIs(
            int index, String number, String date, String type) throws Exception {
        OrderWidget orderWidget = orderList.get(index - 1);
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

    @Step("Проверить, что в списке документов есть документ с номером {number} и статусом {status}")
    public OrdersListPage shouldListContainsOrderWithNumberAndStatus(String number, String status) {
        String ps = getPageSource();
        for (OrderWidget orderWidget : orderList) {
            if (orderWidget.numberLbl.getText(ps).equals(number)) {
                anAssert.isEquals(orderWidget.typeLbl.getText(ps), status,
                        String.format("Документ с номером %s имеет неверный статус", number));
                return this;
            }
        }
        anAssert.isTrue(false, String.format("Не найден документ с номером %s", number));
        return this;
    }

}
