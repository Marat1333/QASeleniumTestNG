package com.leroy.magportal.ui.pages.cart_estimate.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.TextArea;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import io.qameta.allure.Step;

public class SendEstimateToEmailModal extends MagPortalBasePage {

    private static final String MODAL_WINDOW_XPATH = "//div[contains(@class, 'Common-ConfirmModal__modal__container')]";
    private static final String MODAL_BACKDROP_XPATH = "//div[contains(@class, 'Modal-backdrop')]";

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//button[contains(@class, 'exitBtn')]", metaName = "Кнопка крестик (закрыть)")
    Button closeBtn;

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//input[@id='emails[0]']", metaName = "Поле 'email' 1")
    EditBox email1Fld;

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//input[@id='emails[1]']", metaName = "Поле 'email' 2")
    EditBox email2Fld;

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//input[@id='emails[2]']", metaName = "Поле 'email' 3")
    EditBox email3Fld;

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//div[contains(@class, 'Bordered__tooltipContainer')]//span",
            metaName = "Подсказки ошибки рядом с полем email")
    Element errorTooltip;

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//button[descendant::span[contains(text(), 'Добавить еще')]]",
            metaName = "Кнопка 'Добавить еще email'")
    Button addOneMoreEmailBtn;

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//textarea[@id='textAreaId']", metaName = "Поле комментарий")
    TextArea commentFld;

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//button[contains(@class, 'Common-ConfirmModal__modal__okButton')]",
            metaName = "Кнопка Применить (Сохранить)")
    Button sendBtn;

    // Actions

    @Step("Заполнить поле email #{index}")
    public SendEstimateToEmailModal enterEmail(int index, String value) {
        switch (index) {
            case 1:
                email1Fld.clearFillAndSubmit(value);
                break;
            case 2:
                email2Fld.clearFillAndSubmit(value);
                break;
            case 3:
                email3Fld.clearFillAndSubmit(value);
                break;
        }
        return this;
    }

    @Step("Заполнить поле комментарий")
    public void enterComment(String value) {
        commentFld.clearFillAndSubmit(value);
    }

    @Step("Нажать кнопку 'Добавить еще email'")
    public void clickAddOneMoreEmailButton() {
        addOneMoreEmailBtn.click();
    }

    @Step("Нажать кнопку 'Отправить'")
    public void clickSendButton() {
        sendBtn.click();
    }

    // Verifications

    @Step("Проверить, что поле email #{index} содержит email {expectedEmail}")
    public SendEstimateToEmailModal shouldEmailFieldIs(int index, String expectedEmail) {
        String actualEmail = null;
        switch (index) {
            case 1:
                actualEmail = email1Fld.getText();
                break;
            case 2:
                actualEmail = email2Fld.getText();
                break;
            case 3:
                actualEmail = email3Fld.getText();
                break;
            default:
                throw new IllegalArgumentException("Всего может быть только 3 поля email");
        }
        anAssert.isEquals(actualEmail, expectedEmail, "Ожидался другой email #" + index);
        return this;
    }

    @Step("Проверить, что подсказка-ошибка отображается под полем email")
    public SendEstimateToEmailModal shouldErrorTooltipIs(String text) {
        anAssert.isElementVisible(errorTooltip);
        anAssert.isEquals(errorTooltip.getText(), text, "Ожидался другой текст ошибки");
        return this;
    }

    @Step("Проверить, что модальное окно 'Отправки сметы на email' отображается корректно")
    public SendEstimateToEmailModal verifyRequiredElements() {
        softAssert.areElementsVisible(email1Fld, commentFld, sendBtn);
        softAssert.verifyAll();
        return this;
    }


}
