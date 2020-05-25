package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.sales.estimate.ActionWithProductCardModalPage;
import io.qameta.allure.Step;

public class CartActionWithProductCardModalPage extends ActionWithProductCardModalPage {

    @AppFindBy(text = "Заменить товар на аналог")
    Element replaceWithAnalog;

    @AppFindBy(text = "Создать скидку")
    Element createDiscount;

    // ACTIONS


    // Verifications

    @Step("Проверить, что страница 'Действия с товаром' отображается корректно")
    public CartActionWithProductCardModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, changeQuantityMenuItem, addProductAgainMenuItem,
                detailsAboutProductMenuItem, removeProductMenuItem, replaceWithAnalog, createDiscount);
        softAssert.verifyAll();
        return this;
    }

}
