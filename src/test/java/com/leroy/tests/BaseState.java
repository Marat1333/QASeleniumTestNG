package com.leroy.tests;

import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.EnvironmentConfigurator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseState extends EnvironmentConfigurator {

    protected CustomSoftAssert softAssert;
    protected String TC_ID;

    private String getTestCaseId(String text) {
        String result = "undefined";
        Matcher matcher = Pattern.compile("C\\d+").matcher(text);
        if (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }

    public BaseState() {
    }

    @BeforeMethod
    protected void baseStateBeforeMethod(Method method) throws Exception {
        softAssert = new CustomSoftAssert();
        TC_ID = getTestCaseId(method.getAnnotation(Test.class).description());
        if (!DriverFactory.isAppProfile())
            driver.get("http://dev.prudevlegowp.hq.ru.corp.leroymerlin.com/all");
    }

    @AfterMethod
    public void assertVerificationAfterMethod() {
        softAssert = null;
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
        this.softAssert = null;
    }

}
