package com.leroy.magmobile.ui;

import com.leroy.core.BaseUiTest;
import com.leroy.core.SessionData;
import com.leroy.core.asserts.CustomAssert;
import com.leroy.core.asserts.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import org.openqa.selenium.WebDriver;

public class MagMobileBaseTest extends BaseUiTest {

    protected Context context;
    protected SessionData sessionData;

    @Override
    protected void initContext(WebDriver driver) {
        context = new Context(driver);
        sessionData = new SessionData();
        context.setSessionData(sessionData);
    }

    @Override
    protected void updateContext(WebDriver driver, CustomSoftAssert customSoftAssert, CustomAssert customAssert, StepLog stepLog, String tcId) {
        super.updateContext(driver, customSoftAssert, customAssert, stepLog, tcId);
        context.setSessionData(sessionData);
    }

    @Override
    protected Context getContext() {
        return context;
    }


    @Override
    protected void cleanContext() {
        if (context != null) {
            context.setTcId(null);
            context.setLog(null);
            context.setAnAssert(null);
            context.setSoftAssert(null);
            context.setDriver(null);
        }
    }
}
