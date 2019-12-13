package com.leroy.pages.app.sales;

import com.leroy.tests.app.helpers.BaseAppSteps;
import org.testng.annotations.Test;

public class SalesDocumentsTest extends BaseAppSteps {

    @Test(description = "C3201029 Создание документа продажи")
    public void testC3201029() {
        // Pre-condition
        loginInAndGoTo(SALES_SECTION);
    }

}
