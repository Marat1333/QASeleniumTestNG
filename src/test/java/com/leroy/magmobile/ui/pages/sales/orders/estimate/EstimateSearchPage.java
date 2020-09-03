package com.leroy.magmobile.ui.pages.sales.orders.estimate;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.sales.orders.SalesDocSearchPage;
import io.qameta.allure.Step;

public class EstimateSearchPage extends SalesDocSearchPage {

    @AppFindBy(text = "СОЗДАТЬ СМЕТУ")
    MagMobButton createCartBtn;

    // Verifications

    @Step("Проверить, что страница 'Сметы' отображается корректно")
    public SalesDocSearchPage verifyRequiredElements() {
        String ps = getPageSource();
        softAssert.areElementsVisible(ps, title, createCartBtn);
        softAssert.isEquals(title.getText(ps), "Сметы", "Неверный загаловок экрана");
        softAssert.verifyAll();
        return this;
    }
}
