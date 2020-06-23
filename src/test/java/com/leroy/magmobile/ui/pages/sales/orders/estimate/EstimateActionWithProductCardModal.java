package com.leroy.magmobile.ui.pages.sales.orders.estimate;

import com.leroy.magmobile.ui.pages.sales.orders.ActionWithProductCardModal;
import io.qameta.allure.Step;

public class EstimateActionWithProductCardModal extends ActionWithProductCardModal<EstimatePage> {

    public EstimateActionWithProductCardModal() {
        super(EstimatePage.class);
    }

    // Actions


    // Verifications

    @Step("Проверить, что страница 'Действия с товаром' отображается корректно")
    public EstimateActionWithProductCardModal verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, changeQuantityMenuItem, addProductAgainMenuItem,
                detailsAboutProductMenuItem, removeProductMenuItem);
        softAssert.verifyAll();
        return this;
    }

}