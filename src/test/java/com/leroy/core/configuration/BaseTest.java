package com.leroy.core.configuration;

import com.leroy.core.Context;
import com.leroy.core.ContextProvider;
import com.leroy.core.UserSessionData;
import com.leroy.core.asserts.AssertWrapper;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.core.listeners.TestRailListener;
import com.leroy.core.testrail.helpers.StepLog;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseTest {

    private boolean evalAfterClass = true;
    private boolean evalAfterMethod = true;
    private boolean evalBeforeMethod = true;
    private boolean evalBeforeClass = true;

    private UserSessionData testClassUserSessionDataTemplate;

    /**
     * The setup for session data
     */
    protected abstract UserSessionData initTestClassUserSessionDataTemplate();

    @BeforeClass
    protected void configurationBeforeClass() {
        if (ContextProvider.getContext() == null) {
            ContextProvider.setContext(new Context());
        }
        testClassUserSessionDataTemplate = initTestClassUserSessionDataTemplate();
        ContextProvider.getContext().setUserSessionData(testClassUserSessionDataTemplate);
    }

    @BeforeMethod
    protected void configurationBeforeMethod(Method method) {
        Context context;
        if (ContextProvider.getContext() == null) {
            context = new Context();
            ContextProvider.setContext(context);
        } else {
            context = ContextProvider.getContext();
        }
        StepLog log = new StepLog();
        SoftAssertWrapper softAssert = new SoftAssertWrapper(log);
        AssertWrapper anAssert = new AssertWrapper(log);
        String tcId = getTestCaseId(method.getAnnotation(Test.class).description());
        context.setUserSessionData(testClassUserSessionDataTemplate.copy());
        context.setLog(log);
        context.setAnAssert(anAssert);
        context.setSoftAssert(softAssert);
        context.setTcId(tcId);
        if (TestRailListener.STEPS_INFO != null)
            TestRailListener.STEPS_INFO.put(tcId, log.getStepResults());
    }

    private String getTestCaseId(String text) {
        String result = "undefined";
        Matcher matcher = Pattern.compile("C\\d+").matcher(text);
        if (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }

    protected void step(String msg) {
        ContextProvider.getContext().getLog().step(msg);
    }

    protected AssertWrapper anAssert() {
        return ContextProvider.getContext().getAnAssert();
    }

    protected SoftAssertWrapper softAssert() {
        return ContextProvider.getContext().getSoftAssert();
    }

    protected UserSessionData getUserSessionData() {
        return ContextProvider.getContext().getUserSessionData();
    }

    // Settings for @Before / @After

    protected boolean isEvalAfterClass() {
        return evalAfterClass;
    }

    protected void setEvalAfterClass(boolean evalAfterClass) {
        this.evalAfterClass = evalAfterClass;
    }

    protected boolean isEvalAfterMethod() {
        return evalAfterMethod;
    }

    protected void setEvalAfterMethod(boolean evalAfterMethod) {
        this.evalAfterMethod = evalAfterMethod;
    }

    protected boolean isEvalBeforeMethod() {
        return evalBeforeMethod;
    }

    protected void setEvalBeforeMethod(boolean evalBeforeMethod) {
        this.evalBeforeMethod = evalBeforeMethod;
    }

    protected boolean isEvalBeforeClass() {
        return evalBeforeClass;
    }

    protected void setEvalBeforeClass(boolean evalBeforeClass) {
        this.evalBeforeClass = evalBeforeClass;
    }

    protected void disableAllCheck() {
        evalAfterMethod = false;
        evalBeforeMethod = false;
        evalAfterClass = false;
        evalBeforeClass = false;
    }

    protected void enableAllCheck() {
        evalAfterMethod = true;
        evalBeforeMethod = true;
        evalAfterClass = true;
        evalBeforeClass = true;
    }

}
