package com.leroy.temp_ui;

import com.leroy.constants.EnvConstants;
import com.leroy.core.TestContext;
import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.EnvironmentConfigurator;
import com.leroy.core.listeners.TestRailListener;
import com.leroy.core.testrail.helpers.StepLog;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseState extends EnvironmentConfigurator {

    protected TestContext context;
    protected StepLog log;

    private String getTestCaseId(String text) {
        String result = "undefined";
        Matcher matcher = Pattern.compile("C\\d+").matcher(text);
        if (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }

    public BaseState() {
        setEvalAfterMethod(false);
    }

    @BeforeMethod
    protected void baseStateBeforeMethod(Method method) throws Exception {
        log = new StepLog();
        CustomSoftAssert softAssert = new CustomSoftAssert(log);
        CustomAssert anAssert = new CustomAssert(log);
        String tcId = getTestCaseId(method.getAnnotation(Test.class).description());
        context = new TestContext(driver, softAssert, anAssert, log, tcId);
        if (TestRailListener.STEPS_INFO != null)
            TestRailListener.STEPS_INFO.put(tcId, log.getStepResults());
        if (!DriverFactory.isAppProfile())
            openStartPage();
    }

    @AfterMethod
    public void baseStateAfterMethod() {
        cleanUp();
    }

    @AfterClass
    protected void baseStateAfterClass() {
        if (this.isEvalAfterClass()) {
            cleanUp();
        }
    }

    private void openStartPage() {
        driver.get(EnvConstants.URL_MAG_PORTAL);
    }

    private void cleanUp() {
        if (this.driver != null) {
            this.driver.quit();
            this.driver = null;
        }
        context.setSoftAssert(null);
        context.setAnAssert(null);
    }

}
