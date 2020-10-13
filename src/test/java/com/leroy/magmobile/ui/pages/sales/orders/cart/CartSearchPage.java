package com.leroy.magmobile.ui.pages.sales.orders.cart;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.sales.orders.SalesDocSearchPage;
import io.qameta.allure.Step;

/**
 * Экран "Корзины" со списком корзин
 */
public class CartSearchPage extends SalesDocSearchPage {

    @AppFindBy(text = "СОЗДАТЬ КОРЗИНУ")
    MagMobButton createCartBtn;

    // Action

    @Step("Нажать на кнопку 'Создать корзину'")
    public Cart35Page clickCreateCartButton() {
        createCartBtn.click();
        return new Cart35Page();
    }

    // Verifications

    @Step("Проверить, что страница 'Корзины' отображается корректно")
    public SalesDocSearchPage verifyRequiredElements() {
        String ps = getPageSource();
        softAssert.areElementsVisible(ps, title, createCartBtn);
        softAssert.isEquals(title.getText(ps), "Корзины", "Неверный загаловок экрана");
        softAssert.verifyAll();
        return this;
    }

}
