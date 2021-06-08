package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.magportal.ui.models.orders.ControlProductCardData;
import com.leroy.magportal.ui.pages.orders.widget.OrderProductControlCardWidget;
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

    @Step("Проверить, что кол-во 'Заказано' у {index}-ого товара равно {value}")
    public ControlOrderPage shouldOrderedQuantityIs(int index, double value) throws Exception {
        index--;
        anAssert.isEquals(Double.parseDouble(productCards.get(index).getOrderedQuantity()), value,
                "Неверное кол-во 'Заказано' у " + (index + 1) + "-ого товара");
        return this;
    }

    @Step("Проверить, что кол-во 'Собрано' у {index}-ого товара равно {value}")
    public ControlOrderPage shouldPickedQuantityIs(int index, double value) throws Exception {
        index--;
        anAssert.isEquals(Double.parseDouble(productCards.get(index).getCollectedQuantity()),value,
                "Неверное кол-во 'Собрано' у " + (index + 1) + "-ого товара");
        return this;
    }

    @Step("Проверить, что кол-во 'Контроль' у {index}-ого товара равно {value}")
    public ControlOrderPage shouldControlledQuantityIs(int index, double value) throws Exception {
        index--;
        anAssert.isEquals(Double.parseDouble(productCards.get(index).getControlledQuantity()), value,
                "Неверное кол-во 'Контроль' у " + (index + 1) + "-ого товара");
        return this;
    }


    
    @Step("Проверить, кол-во 'Заказано', 'Собрано', 'Контроль' у {index}-ого товара равно {value}")
    public ControlOrderPage orderPageVerifications(int index, double value) throws Exception {
        index--;
        softAssert.isEquals(productCards.get(index).getOrderedQuantity(), String.valueOf(value),
                "Неверное кол-во 'Заказано' у " + (index + 1) + "-ого товара");
        softAssert.isEquals(productCards.get(index).getCollectedQuantity(),String.valueOf(value),
                "Неверное кол-во 'Собрано' у " + (index + 1) + "-ого товара");
        softAssert.isEquals(productCards.get(index).getControlledQuantity(), String.valueOf(value),
                "Неверное кол-во 'Контроль' у " + (index + 1) + "-ого товара");
        return this;
    }

}
    

