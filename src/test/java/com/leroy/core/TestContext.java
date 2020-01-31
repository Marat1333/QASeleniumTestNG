package com.leroy.core;

import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import org.openqa.selenium.WebDriver;

public class TestContext {

    private CustomSoftAssert softAssert;
    private CustomAssert anAssert;
    private String tcId;
    private StepLog log;
    private WebDriver driver;

    // Specific crutch for MagMobile
    private boolean is35Shop;

    public boolean isIs35Shop() {
        return is35Shop;
    }

    public void setIs35Shop(boolean is35Shop) {
        this.is35Shop = is35Shop;
    }
    ///

    public TestContext(WebDriver driver, CustomSoftAssert softAssert, CustomAssert anAssert, StepLog log, String tcId) {
        this.driver = driver;
        this.softAssert = softAssert;
        this.anAssert = anAssert;
        this.log = log;
        this.tcId = tcId;
    }

    public CustomAssert getAnAssert() {
        return anAssert;
    }

    public void setAnAssert(CustomAssert anAssert) {
        this.anAssert = anAssert;
    }

    public CustomSoftAssert getSoftAssert() {
        return softAssert;
    }

    public void setSoftAssert(CustomSoftAssert softAssert) {
        this.softAssert = softAssert;
    }

    public String getTcId() {
        return tcId;
    }

    public void setTcId(String tcId) {
        this.tcId = tcId;
    }

    public StepLog getLog() {
        return log;
    }

    public void setLog(StepLog log) {
        this.log = log;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}
