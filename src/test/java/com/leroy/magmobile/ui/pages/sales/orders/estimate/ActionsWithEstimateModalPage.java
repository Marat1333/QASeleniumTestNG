package com.leroy.magmobile.ui.pages.sales.orders.estimate;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import io.qameta.allure.Step;

public class ActionsWithEstimateModalPage extends CommonMagMobilePage {

    @AppFindBy(text = "Действия со сметой")
    Element headerLbl;

    @AppFindBy(text = "Отправить на email")
    Element sendEmailMenuItem;

    @AppFindBy(text = "Преобразовать в корзину")
    Element transformToBasketMenuItem;

    @AppFindBy(text = "Отправить ссылку на телефон")
    Element sendReferenceToPhoneMenuItem;

    @AppFindBy(text = "Распечатать")
    Element printMenuItem;

    // Actions

    @Step("Выберите пункт меню 'Преобразовать в корзину'")
    public Cart35Page clickTransformToBasketMenuItem() {
        transformToBasketMenuItem.click();
        return new Cart35Page();
    }

    @Step("Выберите пункт меню 'Отправить на email'")
    public SendEmailPage clickSendEmailMenuItem() {
        sendEmailMenuItem.click();
        return new SendEmailPage();
    }


    // Verifications

    @Step("Проверить, что страница 'Действия со сметой' отображается корректно")
    public ActionsWithEstimateModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, sendEmailMenuItem, transformToBasketMenuItem,
                sendReferenceToPhoneMenuItem, printMenuItem);
        softAssert.verifyAll();
        return this;
    }


}
