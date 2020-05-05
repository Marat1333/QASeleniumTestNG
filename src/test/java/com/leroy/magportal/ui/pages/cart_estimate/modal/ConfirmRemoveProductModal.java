package com.leroy.magportal.ui.pages.cart_estimate.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import io.qameta.allure.Step;

public class ConfirmRemoveProductModal extends MagPortalBasePage {

    public ConfirmRemoveProductModal(Context context) {
        super(context);
    }

    private final String MODAL_DIV_XPATH = "//div[contains(@class, 'Modal-contentFill-wrapper')]";

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[1]/button", metaName = "Кнопка 'Нет'")
    Button noBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[2]/button", metaName = "Кнопка 'Да'")
    Button yesBtn;

    @Override
    public void waitForPageIsLoaded() {
        noBtn.waitForVisibility();
        yesBtn.waitForVisibility();
    }

    @Step("Проверить, что модальное окно о подтверждении удаления товара отображается корректно")
    public ConfirmRemoveProductModal verifyRequiredElements() {
        softAssert.isElementTextEqual(noBtn, "НЕТ, ОСТАВИТЬ");
        softAssert.isElementTextEqual(yesBtn, "УДАЛИТЬ");
        softAssert.verifyAll();
        return this;
    }

    @Step("Подтвердить удаление товара")
    public void clickYesButton() {
        yesBtn.click();
    }
}
