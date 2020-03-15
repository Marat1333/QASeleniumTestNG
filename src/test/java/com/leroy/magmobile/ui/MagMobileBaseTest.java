package com.leroy.magmobile.ui;

import com.leroy.core.BaseUiTest;
import com.leroy.core.asserts.CustomAssert;
import com.leroy.core.asserts.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import org.openqa.selenium.WebDriver;

public class MagMobileBaseTest extends BaseUiTest {

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
