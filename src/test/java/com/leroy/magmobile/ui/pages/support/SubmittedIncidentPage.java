package com.leroy.magmobile.ui.pages.support;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class SubmittedIncidentPage extends BaseAppPage {

    public SubmittedIncidentPage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.widget.TextView[1]", metaName = "Заголовок 'Письмо отправлено. Спасибо'")
    private Element headerLbl;

    @AppFindBy(xpath = "//android.widget.TextView[2]", metaName = "Текст с номером инцидента")
    private Element incidentCreatedWithNumberLbl;

    @AppFindBy(xpath = "//android.widget.TextView[3]", metaName = "Текст с почтой, куда будет отправлен email")
    private Element answerWillSendOnMailLbl;

    @AppFindBy(text = "ОТПРАВИТЬ ЕЩЕ ОДНО ПИСЬМО")
    private Element buttonLbl;

    @Override
    public void waitForPageIsLoaded() {
        buttonLbl.waitForVisibility(timeout);
    }

    public String getIncidentNumber() {
        return incidentCreatedWithNumberLbl.getText()
                .replaceAll("Создан инцидент № |\\.", "").trim();
    }

    public String getEmail() {
        return answerWillSendOnMailLbl.getText()
                .replaceAll("Ответ будет выслан на ", "").trim();
    }

    public boolean isIncidentNumberVisibleAndValid() {
        String number = getIncidentNumber();
        return number.matches("INC\\d{6}_\\d{4}");
    }

    /* ------------------------- ACTION STEPS -------------------------- */


    /* ---------------------- Verifications -------------------------- */

    @Step("Проверить, что страница о созданном инциденте создана отображается успешно")
    public SubmittedIncidentPage verifyVisibilityOfAllElements() {
        softAssert.isElementTextEqual(headerLbl, "Письмо отправлено.\nСпасибо!");
        softAssert.isTrue(isIncidentNumberVisibleAndValid(), "Должен быть виден номер инцидента");
        softAssert.isElementTextEqual(buttonLbl, "ОТПРАВИТЬ ЕЩЕ ОДНО ПИСЬМО");
        softAssert.isTrue(answerWillSendOnMailLbl.getText().contains("Ответ будет выслан на "),
                "Должен быть информация с email адресом, куда будет отправлен ответ");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить данные на странице. Номер - {incidentNumber}; email - {email}")
    public SubmittedIncidentPage verifyDataOnThePage(String incidentNumber, String email) {
        if (incidentNumber != null)
            softAssert.isEquals(getIncidentNumber(), incidentNumber,
                    "Номер инцидента должен быть %s");
        if (email != null) {
            softAssert.isEquals(getEmail(), email, "email должен быть %s");
        }
        softAssert.verifyAll();
        return this;
    }

}
