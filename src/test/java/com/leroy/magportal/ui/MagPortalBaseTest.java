package com.leroy.magportal.ui;

import com.leroy.constants.EnvConstants;
import com.leroy.core.BaseUiTest;
import com.leroy.core.TestContext;
import com.leroy.core.asserts.CustomAssert;
import com.leroy.core.asserts.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public class MagPortalBaseTest extends BaseUiTest {

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
