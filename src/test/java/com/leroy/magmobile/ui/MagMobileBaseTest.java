package com.leroy.magmobile.ui;

import com.leroy.core.BaseUiTest;
import com.leroy.core.SessionData;
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
    protected Context getContext() {
        return context;
    }


    @Override
    protected void cleanContext() {
        context = null;
    }
}
