package com.leroy.magmobile.api.tests.common;

import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.magmobile.api.SessionData;
import org.testng.annotations.BeforeMethod;
import ru.leroymerlin.qa.core.base.Base;

import java.lang.reflect.Method;

public class BaseProjectTest extends Base {

    protected SessionData sessionData;
    protected StepLog log;

    @BeforeMethod
    protected void baseStateBeforeMethod(Method method) throws Exception {
        log = new StepLog();
    }

}
