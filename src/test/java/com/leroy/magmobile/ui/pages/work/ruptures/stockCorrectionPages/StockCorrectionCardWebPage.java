package com.leroy.magmobile.ui.pages.work.ruptures.stockCorrectionPages;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class StockCorrectionCardWebPage extends BaseWebPage {

    @WebFindBy(xpath = "//*[text()='Отдел']/following-sibling::span")
    Element shopAndDepartment;

    @WebFindBy(xpath = "//*[text()='Причина коррекции']/following-sibling::span")
    Element reason;

    @WebFindBy(text = "отправить")
    Element sendBtn;

    @WebFindBy(text = "ОТПРАВИТЬ")
    Element confirmSendBtn;

    @Override
    protected void waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        shopAndDepartment.waitForVisibility();
    }

    @Step("Проверить отдел и магазин")
    public void checkShopAndDepartment() {
        anAssert.isElementTextEqual(shopAndDepartment, "35 - 5 - Напольные покрытия");
    }

    @Step("Проверить причину коррекции")
    public void checkReason() {
        anAssert.isElementTextEqual(reason, "Перебои");
    }

    @Step("Проверить ЛМ-код")
    public void checkLmCode(String lmCode) {
        anAssert.isElementVisible(E("//*[contains(text(), '" +lmCode+ "')]", "lmCode"));
    }

    @Step("Нажать \"отправить\"")
    public void clickSendBtn() {
        sendBtn.clickJS();
    }

    @Step("Нажать кнопку \"отправить\" в модалке")
    public StockCorrectionSuccessWebPage clickConfirmSendBtn() {
        confirmSendBtn.clickJS();
        return new StockCorrectionSuccessWebPage();
    }
}