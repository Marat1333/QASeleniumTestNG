package com.leroy.magmobile.ui.pages.sales.orders.cart;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.sales.orders.estimate.ActionWithProductCardModalPage;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartActionWithProductCardModalPage extends ActionWithProductCardModalPage {

    @AppFindBy(text = "Заменить товар на аналог")
    Element replaceWithAnalog;

    @AppFindBy(text = "Создать скидку")
    Element createDiscount;

    @AppFindBy(text = "Изменить скидку")
    Element changeDiscount;

    // ACTIONS

    @Step("Выберите пункт меню 'Создать скидку'")
    public DiscountPage clickCreateDiscountMenuItem() {
        createDiscount.click();
        return new DiscountPage();
    }

    @Step("Выберите пункт меню 'Изменить скидку'")
    public DiscountPage clickChangeDiscountMenuItem() {
        changeDiscount.click();
        return new DiscountPage();
    }

    // Verifications

    @Step("Проверить, что страница 'Действия с товаром' отображается корректно")
    public CartActionWithProductCardModalPage verifyRequiredElements(boolean hasDiscount) {
        List<Element> expectedElements = new ArrayList<>(Arrays.asList(headerLbl, changeQuantityMenuItem, addProductAgainMenuItem,
                detailsAboutProductMenuItem, removeProductMenuItem, replaceWithAnalog));
        if (hasDiscount)
            expectedElements.add(changeDiscount);
        else
            expectedElements.add(createDiscount);
        softAssert.areElementsVisible(expectedElements.toArray(new Element[0]));
        softAssert.verifyAll();
        return this;
    }

    public CartActionWithProductCardModalPage verifyRequiredElements() {
        return verifyRequiredElements(false);
    }

}
