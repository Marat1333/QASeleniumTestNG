package com.leroy.magportal.ui;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.BaseUiTest;
import com.leroy.core.SessionData;
import com.leroy.core.TestContext;
import com.leroy.core.api.Module;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.api.ApiClientProvider;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Guice;

@Guice(modules = {Module.class})
public class MagPortalBaseTest extends BaseUiTest {

    protected Context context;
    protected SessionData sessionData;

    @Inject
    protected ApiClientProvider apiClientProvider;


    @Override
    protected void cleanContext() {
        context = null;
    }

    @Override
    protected void initContext(WebDriver driver) {
        context = new Context(driver);
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId("35");
        sessionData.setUserDepartmentId("1");
        context.setSessionData(sessionData);
        apiClientProvider.setSessionData(sessionData);
    }

    @Override
    protected TestContext getContext() {
        return context;
    }

}
