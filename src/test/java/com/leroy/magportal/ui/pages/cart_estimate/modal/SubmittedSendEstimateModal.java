package com.leroy.magportal.ui.pages.cart_estimate.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SubmittedSendEstimateModal extends MagPortalBasePage {

    private static final String MODAL_WINDOW_XPATH = "//div[contains(@class, 'Common-ConfirmModal__modal__container')]";
    private static final String MODAL_BACKDROP_XPATH = "//div[contains(@class, 'Modal-backdrop')]";

    @WebFindBy(xpath = "//span[contains(text(), 'Смета отправлена')]", metaName = "Основной текст окна")
    Element mainText;

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//button[contains(@class, 'Common-ConfirmModal__modal__okButton')]",
            metaName = "Кнопка 'Понятно'")
    Button confirmBtn;

    // Actions

    @Step("Нажать кнопку понятно")
    public void clickConfirmButton() {
        confirmBtn.click();
    }

    // Verifications

    @Step("Проверить, что модальное окно 'Экран успеха отправления сметы на email' отображается корректно")
    public SubmittedSendEstimateModal verifyRequiredElements() {
        softAssert.areElementsVisible(mainText, confirmBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что смета была отправлена на email - {expectedEmails}")
    public SubmittedSendEstimateModal shouldSentToEmail(String... expectedEmails) {
        List<String> actualEmails = ParserUtil.extractEmailsFromString(mainText.getText());
        anAssert.isEquals(new HashSet<>(actualEmails), new HashSet<>(Arrays.asList(expectedEmails)),
                "Ожидались другие email'ы");
        return this;
    }

}
