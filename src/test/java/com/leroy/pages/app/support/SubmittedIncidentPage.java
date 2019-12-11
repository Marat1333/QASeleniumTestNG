package com.leroy.pages.app.support;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.app.common.BottomMenuPage;
import org.openqa.selenium.WebDriver;

public class SubmittedIncidentPage extends BottomMenuPage {

    public SubmittedIncidentPage(WebDriver driver) {
        super(driver);
    }

    @AppFindBy(xpath = "//android.widget.TextView[1]")
    public Element headerLbl;

    @AppFindBy(xpath = "//android.widget.TextView[2]")
    public Element incidentCreatedWithNumberLbl;

    @AppFindBy(xpath = "//android.widget.TextView[3]")
    public Element answerWillSendOnMailLbl;

    @AppFindBy(text = "ОТПРАВИТЬ ЕЩЕ ОДНО ПИСЬМО")
    public Element buttonLbl;

    @Override
    public void waitForPageIsLoaded() {
        buttonLbl.waitForVisibility(timeout);
    }

    public String getIncidentNumber() {
        return incidentCreatedWithNumberLbl.getText().replaceAll("Создан инцидент № |\\.", "").trim();
    }

    public String getEmail() {
        return answerWillSendOnMailLbl.getText().replaceAll("Ответ будет выслан на ", "").trim();
    }

    public boolean isIncidentNumberVisibleAndValid() {
        String number = getIncidentNumber();
        return number.matches("INC\\d{6}_\\d{4}");
    }

}
