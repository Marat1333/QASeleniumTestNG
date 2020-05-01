package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.common.MenuPage;
import io.qameta.allure.Step;

public class CartPage extends MenuPage {

    public CartPage(Context context) {
        super(context);
    }

    @WebFindBy(xpath = "//button[descendant::span[text()='Создать корзину']]",
            metaName = "Текст кнопки 'Создать корзину'")
    Element createCartBtn;

    // Actions

    @Step("Нажать кнопку 'Создать корзину'")
    public CreateCartPage clickCreateCartButton() {
        createCartBtn.click();
        return new CreateCartPage(context);
    }
}
