package com.leroy.magmobile.api.tests;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.core.SessionData;
import com.leroy.core.api.Module;
import com.leroy.core.listeners.TestRailListener;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.magmobile.api.ApiClientProvider;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.umbrella_extension.authorization.AuthClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Guice(modules = {Module.class})
public abstract class BaseProjectApiTest {

    @Inject
    private Provider<CatalogSearchClient> searchClientProvider;

    public CatalogSearchClient getCatalogSearchClient() {
        CatalogSearchClient searchClient = searchClientProvider.get();
        searchClient.setSessionData(sessionData);
        return searchClient;
    }

    @Inject
    private AuthClient authClient;

    @Inject
    protected ApiClientProvider apiClientProvider;

    protected SessionData sessionData;
    protected StepLog log;

    private String getTestCaseId(String text) {
        String result = "undefined";
        Matcher matcher = Pattern.compile("C\\d+").matcher(text);
        if (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }

    @BeforeClass
    protected void setUpDefaultSessionData() {
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId(EnvConstants.SHOP_WITH_NEW_INTERFACE);
        sessionData.setUserDepartmentId("1");
        if (isNeedAccessToken()) {
            sessionData.setAccessToken(authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
                    EnvConstants.BASIC_USER_PASS));
        }
        apiClientProvider.setSessionData(sessionData);
    }

    protected boolean isNeedAccessToken() {
        return false;
    }

    @BeforeMethod
    protected void baseTestBeforeMethod(Method method) throws Exception {
        log = new StepLog();
        String tcId = getTestCaseId(method.getAnnotation(Test.class).description());
        if (TestRailListener.STEPS_INFO != null)
            TestRailListener.STEPS_INFO.put(tcId, log.getStepResults());
    }

    protected void step(String step) {
        log.step(step);
    }

}
