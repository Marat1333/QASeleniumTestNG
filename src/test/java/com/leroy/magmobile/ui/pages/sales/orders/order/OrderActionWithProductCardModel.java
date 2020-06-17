package com.leroy.magmobile.ui.pages.sales.orders.order;

import com.leroy.magmobile.ui.pages.sales.orders.ActionWithProductCardModal;
import com.leroy.magmobile.ui.pages.sales.orders.CartOrderEstimatePage;
import io.qameta.allure.Step;

public class OrderActionWithProductCardModel<T extends CartOrderEstimatePage> extends ActionWithProductCardModal<T> {

    public OrderActionWithProductCardModel(Class<T> type) {
        super(type);
    }

    // Actions


    // Verifications

    @Step("Проверить, что страница 'Действия с товаром' отображается корректно")
    public OrderActionWithProductCardModel<T> verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, changeQuantityMenuItem, addProductAgainMenuItem,
                detailsAboutProductMenuItem, removeProductMenuItem);
        softAssert.verifyAll();
        return this;
    }
}
