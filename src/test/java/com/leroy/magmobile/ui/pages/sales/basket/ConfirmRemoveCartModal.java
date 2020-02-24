package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import io.qameta.allure.Step;

public class ConfirmRemoveCartModal extends CommonMagMobilePage {

    public ConfirmRemoveCartModal(Context context) {
        super(context);
    }

    @AppFindBy(text = "Выйти и удалить корзину?")
    Element headerLbl;

    @AppFindBy(text = "ВЫЙТИ")
    Element yesBtn;

    @AppFindBy(text = "ОТМЕНА")
    Element noBtn;

    // Actions
    @Step("Нажмите кнопку для подтверждения 'Выйти'")
    public SalesDocumentsPage clickConfirmButton() {
        yesBtn.click();
        return new SalesDocumentsPage(context);
    }

    // Verifications

    @Step("Проверить, что модальное окно с подтверждением удаления 'Выйти и удалить корзину?' отображается корректно")
    public ConfirmRemoveCartModal verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, yesBtn, noBtn);
        softAssert.verifyAll();
        return this;
    }

}
