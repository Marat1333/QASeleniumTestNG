package com.leroy.magmobile.ui;

import com.leroy.core.TestContext;
import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.SessionData;
import org.openqa.selenium.WebDriver;

public class Context extends TestContext {

    private SessionData sessionData;

    public Context(WebDriver driver, CustomSoftAssert softAssert, CustomAssert anAssert, StepLog log, String tcId) {
        super(driver, softAssert, anAssert, log, tcId);
        sessionData = new SessionData();
    }

    public boolean isNewShopFunctionality() {
        return sessionData.getUserShopId().equals("35");
    }

    public SessionData getSessionData() {
        return sessionData;
    }

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }
}
