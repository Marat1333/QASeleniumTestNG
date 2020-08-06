package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public abstract class OrderDraftPage extends OrderHeaderPage {

    @WebFindBy(xpath = "//div[contains(@class, 'OrderCreate__header')]//span[contains(@class, 'Status-container')]",
            metaName = "Статус заказа")
    Element documentStatus;

    @Step("Проверить, что статус заказа = {value}")
    public OrderDraftPage shouldOrderStatusIs(String value) {
        anAssert.isEquals(documentStatus.getText().toLowerCase(), value.toLowerCase(), "Неверный статусс заказа");
        return this;
    }

}
