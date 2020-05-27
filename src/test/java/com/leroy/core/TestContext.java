package com.leroy.core;

import com.leroy.core.asserts.AssertWrapper;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.core.testrail.helpers.StepLog;
import lombok.Data;
import org.openqa.selenium.WebDriver;

@Data
public class TestContext {
    private SoftAssertWrapper softAssert;
    private AssertWrapper anAssert;
    private String tcId;
    private StepLog log;
    private WebDriver driver;
    private UserSessionData userSessionData;
}
