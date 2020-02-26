package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.pages.sales.ProductCardPage;
import io.qameta.allure.Step;

public class SimilarProductsPage extends ProductCardPage {

    public SimilarProductsPage(Context context) {
        super(context);
    }


    // Verifications

    @Step("Проверить, что карточки товаров имеют соответсвующий вид для фильтра 'Вся гамма ЛМ'")
    public SimilarProductsPage verifyProductCardsHaveAllGammaView() {
        anAssert.isFalse(E("за штуку").isVisible(), "Карточки товаров не должны содержать цену");
        anAssert.isFalse(E("доступно").isVisible(), "Карточки товаров не должны содержать доступное кол-во");
        return this;
    }

}
