package com.leroy.magmobile.ui.pages.sales;

import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.pages.sales.estimate.EstimatePage;
import io.qameta.allure.Step;

public class EditProduct35Page extends AddProduct35Page {

    @Override
    protected String SCREEN_TITLE_VALUE() {
        return "Изменение количества";
    }

    public EditProduct35Page(Context context) {
        super(context);
    }

    // ACTIONS

    @Step("Нажмите кнопку сохранить")
    public EstimatePage clickSaveButton() {
        submitBtn.click();
        return new EstimatePage(context);
    }

}
