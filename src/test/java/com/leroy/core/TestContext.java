package com.leroy.core;

import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.testrail.helpers.StepLog;
import org.openqa.selenium.WebDriver;

public class TestContext {

    private CustomSoftAssert softAssert;
    private CustomAssert anAssert;
    private String TC_ID;
    private StepLog log;
    private WebDriver driver;

    public TestContext(WebDriver driver, CustomSoftAssert softAssert, CustomAssert anAssert, StepLog log, String tcId) {
        this.driver = driver;
        this.softAssert = softAssert;
        this.anAssert = anAssert;
        this.log = log;
        this.TC_ID = tcId;
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

    public String getTC_ID() {
        return TC_ID;
    }

    public void setTC_ID(String TC_ID) {
        this.TC_ID = TC_ID;
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
