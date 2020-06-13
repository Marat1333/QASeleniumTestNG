package com.leroy.magmobile.ui.pages.sales.orders.order;

import com.leroy.magmobile.ui.pages.sales.orders.ActionWithProductCardModal;
import io.qameta.allure.Step;

public class OrderActionWithProductCardModel extends ActionWithProductCardModal<CartProcessOrder35Page> {

    public OrderActionWithProductCardModel() {
        super(CartProcessOrder35Page.class);
    }

    // Actions


    // Verifications

    @Step("Проверить, что страница 'Действия с товаром' отображается корректно")
    public OrderActionWithProductCardModel verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, changeQuantityMenuItem, addProductAgainMenuItem,
                detailsAboutProductMenuItem, removeProductMenuItem);
        softAssert.verifyAll();
        return this;
    }
}
