package com.leroy.magmobile.ui.pages.sales.orders.cart.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import io.qameta.allure.Step;

public class ChangeProductModal extends CommonMagMobilePage {

    @AppFindBy(text = "Товара достаточно в магазине")
    Element enoughProductsInStoreMenuItem;

    @AppFindBy(text = "Уменьшить до доступного кол-ва")
    Element reduceToAvailableQuantityMenuItem;

    @AppFindBy(text = "Разделить на два заказа")
    Element splitToTwoOrdersMenuItem;

    // ACTIONS

    @Step("Выберите пункт меню 'Товара достаточно в магазине'")
    public Cart35Page clickEnoughProductInStore() {
        enoughProductsInStoreMenuItem.click();
        Cart35Page cart35Page = new Cart35Page();
        waitUntilProgressBarIsInvisible();
        return cart35Page;
    }

    // Verifications
    @Step("Проверить, что модальное окно 'Изменение кол-ва товара' отображается корректно")
    public ChangeProductModal verifyRequiredElements() {
        softAssert.areElementsVisible(
                enoughProductsInStoreMenuItem, reduceToAvailableQuantityMenuItem,
                splitToTwoOrdersMenuItem);
        softAssert.verifyAll();
        return this;
    }

}
