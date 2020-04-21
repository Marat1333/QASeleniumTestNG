package com.leroy.magmobile.ui.pages.sales.estimate;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class ConfirmRemoveLastProductEstimateModal extends CommonMagMobilePage {

    public ConfirmRemoveLastProductEstimateModal(Context context) {
        super(context);
    }

    @AppFindBy(text = "Удалить смету?")
    Element headerLbl;

    @AppFindBy(text = "УДАЛИТЬ")
    Element yesBtn;

    @AppFindBy(text = "НЕТ, ОСТАВИТЬ")
    Element noBtn;

    // Actions
    @Step("Нажмите кнопку для подтверждения 'Выйти'")
    public void clickConfirmButton() {
        yesBtn.click();
    }

    // Verifications

    @Step("Проверить, что модальное окно с подтверждением удаления 'Выйти и удалить корзину?' отображается корректно")
    public ConfirmRemoveLastProductEstimateModal verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, yesBtn, noBtn);
        softAssert.verifyAll();
        return this;
    }

}
