package com.leroy.magportal.ui.pages.cart_estimate.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import io.qameta.allure.Step;

public class SubmittedEstimateModal extends MagPortalBasePage {

    public SubmittedEstimateModal(Context context) {
        super(context);
    }

    private final String MODAL_WINDOW_XPATH = "//div[contains(@class, 'ConfirmModal')]";
    private final String MODAL_BACKDROP_XPATH = "//div[contains(@class, 'Modal-backdrop')]";

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//span[contains(@class, 'mainText')]")
    private Element headerLbl;

    @WebFindBy(text = "Номер сметы")
    private Element numberEstimateLbl;

    @WebFindBy(xpath = "//span[text()='Номер сметы']/following-sibling::span")
    private Element numberEstimate;

    @WebFindBy(xpath = "//span[text()='Отправить на email']/../../button")
    private Element sendEmailBtn;

    @WebFindBy(xpath = "//span[text()='Распечатать']/../../button")
    private Element printBtn;

    @Override
    public void waitForPageIsLoaded() {
        E(MODAL_WINDOW_XPATH).waitForVisibility();
    }

    // Action
    @Step("Закрыть окно с информацией о том, что смета создана")
    public EstimatePage closeWindow() {
        E(MODAL_BACKDROP_XPATH).clickJS();
        return new EstimatePage(context);
    }

    // Verifications

    @Step("Проверить, что модальное окно 'Смета создана успешно' отображается корректно")
    public SubmittedEstimateModal verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, numberEstimateLbl, sendEmailBtn, printBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что номер сметы = {val}")
    public SubmittedEstimateModal shouldEstimateNumberIs(String val) {
        anAssert.isEquals(numberEstimate.getText().replaceAll(" ", ""), val,
                "Ожидался другой номер сметы");
        return this;
    }

    @Step("Проверить, что цены зафиксированы на {val} дней")
    public SubmittedEstimateModal shouldPricesAreFixedAt(String val) {
        anAssert.isElementTextEqual(headerLbl, String.format(
                "Смета создана, цены зафиксированы на %s дней", val));
        return this;
    }
}
