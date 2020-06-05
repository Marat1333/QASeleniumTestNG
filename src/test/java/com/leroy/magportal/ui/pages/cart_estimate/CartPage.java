package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.pages.cart_estimate.widget.OrderPuzWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;

public class CartPage extends CartEstimatePage {

    @WebFindBy(xpath = "//button[descendant::span[text()='Создать корзину']]",
            metaName = "Текст кнопки 'Создать корзину'")
    Element createCartBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Cart-CartsView__header-text')]", metaName = "Номер корзины")
    Element cartNumber;

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - 21) = 'CartsView__cart__group']",
            clazz = OrderPuzWidget.class)
    CardWebWidgetList<OrderPuzWidget, OrderWebData> orders;

    @Override
    protected CardWebWidgetList<OrderPuzWidget, OrderWebData> orders() {
        return orders;
    }

    // Grab info

    @Override
    public String getDocumentNumber() {
        return cartNumber.getText();
    }

    @Override
    public String getDocumentStatus() {
        return null; //todo
    }

    @Override
    public String getDocumentAuthor() {
        return null; //todo
    }

    @Override
    public String getCreationDate() {
        return null; // todo
    }

    // Actions

    @Step("Нажать кнопку 'Создать корзину'")
    public CartPage clickCreateCartButton() {
        createCartBtn.click();
        return this;
    }
}
