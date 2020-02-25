package com.leroy.magportal.ui;

import com.leroy.constants.EnvConstants;
import com.leroy.core.BaseTest;
import com.leroy.core.TestContext;
import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public class MagPortalBaseTest extends BaseTest {

    TestContext context;

    @Override
    protected void cleanContext() {
        context = null;
    }

    @Override
    protected void initContext(WebDriver driver, CustomSoftAssert customSoftAssert,
                               CustomAssert customAssert, StepLog stepLog, String tcId) {
        context = new TestContext(driver, customSoftAssert, customAssert, stepLog, tcId);
    }

    private void openStartPage() {
        driver.get(EnvConstants.URL_MAG_PORTAL);
    }

    @BeforeMethod
    protected void magPortalBaseBeforeMethod(Method method) throws Exception {
        openStartPage();
    }

}
