package com.leroy.temp_ui;

import com.leroy.core.TestContext;
import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.configuration.EnvironmentConfigurator;
import com.leroy.core.listeners.TestRailListener;
import com.leroy.core.testrail.helpers.StepLog;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseState extends EnvironmentConfigurator {

    protected StepLog log;
    protected CustomSoftAssert softAssert;
    protected CustomAssert anAssert;

    protected abstract void initContext(
            WebDriver driver, CustomSoftAssert customSoftAssert, CustomAssert customAssert,
            StepLog stepLog, String tcId);

    protected abstract void cleanContext();

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
        softAssert = new CustomSoftAssert(log);
        anAssert = new CustomAssert(log);
        String tcId = getTestCaseId(method.getAnnotation(Test.class).description());
        initContext(driver, softAssert, anAssert, log, tcId);
        if (TestRailListener.STEPS_INFO != null)
            TestRailListener.STEPS_INFO.put(tcId, log.getStepResults());
    }

    @AfterMethod
    public void baseStateAfterMethod() {
        cleanContext();
    }

    @AfterClass
    protected void baseStateAfterClass() {
        if (this.isEvalAfterClass()) {
            cleanUp();
        }
    }

    private void cleanUp() {
        if (this.driver != null) {
            this.driver.quit();
            this.driver = null;
        }
        cleanContext();
    }

}
