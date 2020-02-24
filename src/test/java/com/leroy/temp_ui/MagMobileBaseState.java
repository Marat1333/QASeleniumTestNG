package com.leroy.temp_ui;

import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.magmobile.ui.Context;
import org.openqa.selenium.WebDriver;

public class MagMobileBaseState extends BaseState {

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
