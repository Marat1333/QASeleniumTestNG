package com.leroy.magportal.ui;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.BaseUiTest;
import com.leroy.core.SessionData;
import com.leroy.core.TestContext;
import com.leroy.core.api.Module;
import com.leroy.core.asserts.CustomAssert;
import com.leroy.core.asserts.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.api.ApiClientProvider;
import com.leroy.umbrella_extension.authorization.AuthClient;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Guice;

@Guice(modules = {Module.class})
public class MagPortalBaseTest extends BaseUiTest {

    protected Context context;
    protected SessionData sessionData;

    @Inject
    protected ApiClientProvider apiClientProvider;
    @Inject
    private AuthClient authClient;


    @Override
    protected void cleanContext() {
        if (context != null) {
            context.setTcId(null);
            context.setLog(null);
            context.setAnAssert(null);
            context.setSoftAssert(null);
        }
    }

    @Override
    protected void initContext(WebDriver driver) {
        context = new Context(driver);
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId(EnvConstants.BASIC_USER_SHOP_ID);
        sessionData.setUserDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);
        if (isNeedAccessToken()) {
            sessionData.setAccessToken(getAccessToken());
        }
        context.setSessionData(sessionData);
        apiClientProvider.setSessionData(sessionData);
    }

    @Override
    protected void updateContext(WebDriver driver, CustomSoftAssert customSoftAssert, CustomAssert customAssert, StepLog stepLog, String tcId) {
        super.updateContext(driver, customSoftAssert, customAssert, stepLog, tcId);
        context.setSessionData(sessionData);
    }

    protected boolean isNeedAccessToken() {
        return false;
    }

    protected String getAccessToken() {
        return authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
                EnvConstants.BASIC_USER_PASS);
    }

    @Override
    protected TestContext getContext() {
        return context;
    }

}
