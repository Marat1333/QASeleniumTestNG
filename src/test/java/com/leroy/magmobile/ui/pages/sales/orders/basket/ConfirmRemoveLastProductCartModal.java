package com.leroy.magmobile.ui.pages.sales.orders.basket;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class ConfirmRemoveLastProductCartModal extends CommonMagMobilePage {

    @AppFindBy(text = "Выйти и удалить корзину?")
    Element headerLbl;

    @AppFindBy(text = "ВЫЙТИ")
    Element yesBtn;

    @AppFindBy(text = "ОТМЕНА")
    Element noBtn;

    // Actions
    @Step("Нажмите кнопку для подтверждения 'Выйти'")
    public void clickConfirmButton() {
        yesBtn.click();
    }

    // Verifications

    @Step("Проверить, что модальное окно с подтверждением удаления 'Выйти и удалить корзину?' отображается корректно")
    public ConfirmRemoveLastProductCartModal verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, yesBtn, noBtn);
        softAssert.verifyAll();
        return this;
    }

}
