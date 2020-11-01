package com.leroy.magmobile.ui.pages.sales.orders.order;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.sales.orders.SalesDocSearchPage;
import io.qameta.allure.Step;

public class OrderSearchPage extends SalesDocSearchPage {

    @AppFindBy(text = "ОФОРМИТЬ ПРОДАЖУ")
    MagMobButton createCartBtn;

    // Verifications

    @Step("Проверить, что страница 'Документы продажи' отображается корректно")
    public SalesDocSearchPage verifyRequiredElements() {
        String ps = getPageSource();
        softAssert.areElementsVisible(ps, title, createCartBtn);
        softAssert.isEquals(title.getText(ps), "Документы продажи", "Неверный загаловок экрана");
        softAssert.verifyAll();
        return this;
    }

}
