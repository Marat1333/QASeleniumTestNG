package com.leroy.core;

import com.leroy.core.asserts.CustomAssert;
import com.leroy.core.asserts.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import org.openqa.selenium.WebDriver;

public class TestContext {

    private CustomSoftAssert softAssert;
    private CustomAssert anAssert;
    private String tcId;
    private StepLog log;
    private WebDriver driver;

    public TestContext(WebDriver driver) {
        this.driver = driver;
    }

    public TestContext(WebDriver driver, CustomSoftAssert softAssert, CustomAssert anAssert, StepLog log, String tcId) {
        this(driver);
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
