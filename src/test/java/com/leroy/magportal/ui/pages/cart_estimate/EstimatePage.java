package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.common.MenuPage;
import io.qameta.allure.Step;

public class EstimatePage extends MenuPage {
    public EstimatePage(Context context) {
        super(context);
    }

    @WebFindBy(xpath = "//button[descendant::span[text()='Создать смету']]",
            metaName = "Текст кнопки 'Создать смету'")
    Element createEstimateBtn;

    // Actions

    @Step("Нажать кнопку 'Создать смету'")
    public CreateEstimatePage clickCreateEstimateButton() {
        createEstimateBtn.click();
        return new CreateEstimatePage(context);
    }
}
