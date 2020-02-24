package com.leroy.magmobile.ui;

import com.leroy.core.TestContext;
import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import org.openqa.selenium.WebDriver;

public class Context extends TestContext {

    private String userLdap;
    private String userShopId;
    private String userDepartmentId;

    public Context(WebDriver driver, CustomSoftAssert softAssert, CustomAssert anAssert, StepLog log, String tcId) {
        super(driver, softAssert, anAssert, log, tcId);
    }

    public boolean is35Shop() {
        return getUserShopId().equals("35");
    }

    public String getUserLdap() {
        return userLdap;
    }

    public void setUserLdap(String userLdap) {
        this.userLdap = userLdap;
    }

    public String getUserShopId() {
        return userShopId;
    }

    public void setUserShopId(String userShopId) {
        this.userShopId = userShopId;
    }

    public String getUserDepartmentId() {
        return userDepartmentId;
    }

    public void setUserDepartmentId(String userDepartmentId) {
        this.userDepartmentId = userDepartmentId;
    }
}
