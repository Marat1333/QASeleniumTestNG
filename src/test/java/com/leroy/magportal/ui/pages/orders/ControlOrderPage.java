package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.models.orders.ControlProductCardData;
import com.leroy.magportal.ui.models.orders.ToGiveAwayProductCardData;
import com.leroy.magportal.ui.pages.orders.widget.OrderProductControlCardWidget;
import com.leroy.magportal.ui.pages.orders.widget.OrderProductToGiveAwayCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;

/**
 * Описывает вкладку "Контроль"
 */
public class ControlOrderPage extends OrderCreatedPage {


    @WebFindBy(xpath = "//div[contains(@class, 'Order-GiveAway-Card')]",
            clazz = OrderProductControlCardWidget.class)
    CardWebWidgetList<OrderProductControlCardWidget, ControlProductCardData> productCards;

    // Actions

    @Step("Развернуть поля карточки заказа")
    public ControlOrderPage expandProductCardFields(int index) throws Exception {
        index--;
        productCards.get(index).clickExpandBtn();
        return this;
    }


    // Verifications

    @Step("Проверить, что кол-во 'К выдаче' у {index}-ого товара равно {value}")
    public ControlOrderPage shouldProductToShipQuantityIs(int index, int value) throws Exception {
        index--;
        anAssert.isEquals(productCards.get(index).getToGiveAwayQuantity(), String.valueOf(value),
                "Неверное кол-во 'К выдаче' у " + (index + 1) + "-ого товара");
        return this;
    }
}
    

