package com.leroy.magportal.ui;

import com.leroy.constants.EnvConstants;
import com.leroy.core.BaseUiTest;
import com.leroy.core.SessionData;
import com.leroy.core.TestContext;
import com.leroy.core.asserts.CustomAssert;
import com.leroy.core.asserts.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.magmobile.ui.Context;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public class MagPortalBaseTest extends BaseUiTest {

    Context context;
    protected SessionData sessionData;

    @Override
    protected void cleanContext() {
        context = null;
    }

    @Override
    protected void initContext(WebDriver driver) {
        context = new Context(driver);
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        context.setSessionData(sessionData);
    }

    @Override
    protected TestContext getContext() {
        return context;
    }

    private void openStartPage() {
        driver.get(EnvConstants.URL_MAG_PORTAL);
    }

    @BeforeMethod
    protected void magPortalBaseBeforeMethod(Method method) throws Exception {
        openStartPage();
    }

}
