package com.leroy.magmobile.api.tests;

import com.leroy.core.listeners.TestRailListener;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.SessionData;
import com.leroy.core.api.BaseModule;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Guice(modules = {BaseModule.class})
public class BaseProjectApiTest {

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
