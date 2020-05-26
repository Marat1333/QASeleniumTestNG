package com.leroy.magmobile.ui.pages.sales.estimate;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import io.qameta.allure.Step;

public class EstimateSubmittedPage extends CommonMagMobilePage {

    @AppFindBy(containsText = "Смета создана")
    Element headerLbl;

    @AppFindBy(xpath = "//*[@content-desc='Button'][1]", metaName = "Кнопка 'Отправить на email'")
    Element sendToEmailBtn;
    @AppFindBy(containsText = "на email")
    Element sendToEmailLbl;

    @AppFindBy(text = "ПЕРЕЙТИ В СПИСОК ДОКУМЕНТОВ")
    Element submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
    }

    // ACTIONS

    @Step("Нажмите кнопку 'Перейти в список документов'")
    public SalesDocumentsPage clickSubmitButton() {
        submitBtn.click();
        return new SalesDocumentsPage();
    }

    @Step("Нажать на 'Отправить на email'")
    public SendEmailPage clickSendToEmailButton() {
        sendToEmailBtn.click();
        return new SendEmailPage(context);
    }

    // VERIFICATIONS

    @Step("Проверить, что страница 'Смета создана...' отображается корректно")
    public EstimateSubmittedPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, submitBtn);
        softAssert.verifyAll();
        return this;
    }

}
