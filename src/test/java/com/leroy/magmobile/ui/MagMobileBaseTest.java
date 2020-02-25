package com.leroy.magmobile.ui;

import com.leroy.core.BaseTest;
import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import org.openqa.selenium.WebDriver;

public class MagMobileBaseTest extends BaseTest {

    protected Context context;

    @Override
    protected void initContext(WebDriver driver, CustomSoftAssert customSoftAssert,
                               CustomAssert customAssert, StepLog stepLog, String tcId) {
        context = new Context(driver, customSoftAssert, customAssert, stepLog, tcId);
    }

    @Override
    protected void cleanContext() {
        context = null;
    }
}
