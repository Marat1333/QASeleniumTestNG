package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.MenuPage;
import io.qameta.allure.Step;

public class CartPage extends MenuPage {

    public CartPage(TestContext context) {
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
