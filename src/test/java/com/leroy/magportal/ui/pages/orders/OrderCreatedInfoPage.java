package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class OrderCreatedInfoPage extends OrderCreatedPage {

    @WebFindBy(xpath = "//button[descendant::*[text()='Контакты']]", metaName = "Кнопка 'Контакты'")
    Element contactBtn;

    @WebFindBy(xpath = "//button[descendant::*[text()='Получение']]", metaName = "Кнопка 'Получение'")
    Element receiveBtn;

    @WebFindBy(xpath = "//button[descendant::*[text()='История']]", metaName = "Кнопка 'История'")
    Element historyBtn;

    @WebFindBy(xpath = "//button[descendant::*[text()='Оплата']]", metaName = "Кнопка 'Оплата'")
    Element paymentBtn;

    @WebFindBy(xpath = "//button[descendant::*[text()='Возвраты']]", metaName = "Кнопка 'Возвраты'")
    Element refundsBtn;

    // ----------------- ПОЛУЧЕНИЕ --------------------- //

    @WebFindBy(xpath = "//*[@id='receiveType']/../div[contains(@class, 'valueContainer')]//span",
            metaName = "Способ получения")
    Element deliveryType;

    // Actions

    @Step("Раскрыть/Скрыть область Получение")
    public OrderCreatedInfoPage clickReceiveBtnButton() {
        receiveBtn.click();
        return this;
    }

    // Verifications

    @Step("Проверить, что способ получения = {expectedDeliveryType}")
    public OrderCreatedInfoPage shouldDeliveryTypeIs(String expectedDeliveryType) {
        deliveryType.scrollTo();
        anAssert.isElementTextEqual(deliveryType, expectedDeliveryType);
        return this;
    }

}
