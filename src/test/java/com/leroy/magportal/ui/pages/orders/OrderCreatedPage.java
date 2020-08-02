package com.leroy.magportal.ui.pages.orders;

import io.qameta.allure.Step;

public abstract class OrderCreatedPage extends OrderHeaderPage {

    // Verifications


    @Step("Проверить, что статус заказа - {value}")
    public void shouldOrderStatusIs(String value) {
        anAssert.isEquals(E("//div[contains(@class, 'OrderViewHeader')]//span[contains(@class, 'Status-container')]").getText(),
                value, "Неверный статус заказа");
    }
}
