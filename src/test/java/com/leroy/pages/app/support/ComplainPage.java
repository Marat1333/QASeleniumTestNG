package com.leroy.pages.app.support;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class ComplainPage extends BaseAppPage {

    public ComplainPage(WebDriver driver) {
        super(driver);
    }

    @AppFindBy(text = "Что случилось?")
    public Element whatHappenLbl;
    @AppFindBy(accessibilityId = "title")
    public EditBox whatHappenFld;

    @AppFindBy(text = "Чуть больше подробностей")
    public Element moreInfoLbl;
    @AppFindBy(accessibilityId = "moreInfo")
    public EditBox moreInfoFld;

    @AppFindBy(text = "Эл. почта для ответа")
    public Element emailLbl;
    @AppFindBy(accessibilityId = "email")
    public EditBox emailFld;
    @AppFindBy(xpath = "//android.widget.EditText[@content-desc='email']/following::android.widget.TextView[1]")
    public Element emailDomainLbl;

    @AppFindBy(text = "ОТПРАВИТЬ", cacheLookup = false)
    public EditBox submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        whatHappenLbl.waitForVisibility();
        submitBtn.waitForVisibility();
    }

    public SubmittedIncidentPage clickSubmitBtn() {
        submitBtn.click();
        return new SubmittedIncidentPage(driver);
    }

}
