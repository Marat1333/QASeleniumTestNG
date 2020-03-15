package com.leroy.magmobile.ui;

import com.leroy.core.BaseUiTest;
import com.leroy.core.TestContext;
import com.leroy.core.asserts.CustomAssert;
import com.leroy.core.asserts.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import org.openqa.selenium.WebDriver;

public class MagMobileBaseTest extends BaseUiTest {

    protected Context context;

    @Override
    protected void initContext(WebDriver driver) {
        context = new Context(driver);
    }

    @Override
    protected Context getContext() {
        return context;
    }


    @Override
    protected void cleanContext() {
        context = null;
    }
}
