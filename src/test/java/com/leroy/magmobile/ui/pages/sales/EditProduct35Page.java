package com.leroy.magmobile.ui.pages.sales;

import io.qameta.allure.Step;

public class EditProduct35Page<T> extends AddProduct35Page<T> {

    public EditProduct35Page(Class<T> parentPage) {
        super(parentPage);
    }

    @Override
    protected String SCREEN_TITLE_VALUE() {
        return "Изменение количества";
    }

    // ACTIONS

    @Step("Нажмите кнопку сохранить")
    public T clickSaveButton() throws Exception {
        submitBtn.click();
        return newParentPage();
    }

    // Verifications

    @Step("Проверить, что страница 'Изменение количества товара' отображается корректно")
    public EditProduct35Page<T> verifyRequiredElements() {
        super.verifyRequiredElements(SubmitBtnCaptions.SAVE);
        return this;
    }

}
