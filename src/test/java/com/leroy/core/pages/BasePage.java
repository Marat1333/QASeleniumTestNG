package com.leroy.core.pages;

import com.leroy.core.BaseContainer;
import com.leroy.core.TestContext;
import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.configuration.Log;
import com.leroy.core.testrail.helpers.StepLog;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;

public class BasePage extends BaseContainer {

    protected StepLog log;
    protected CustomSoftAssert softAssert;
    protected CustomAssert anAssert;
    protected TestContext context;

    private static String screenshotPath = System.getProperty("output.path");

    public BasePage(TestContext context) {
        super(context.getDriver());
        this.log = context.getLog();
        this.softAssert = context.getSoftAssert();
        this.anAssert = context.getAnAssert();
        this.context = context;
        initElements();
        waitForPageIsLoaded();
    }

    public void waitForPageIsLoaded() {
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    protected byte[] takeScreenShotAsByteArray() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public String takeScreenShot(String fileName) {
        try {
            Log.info("Screen shot FileName: " + fileName);
            File destFile = new File(screenshotPath + File.separator + fileName);
            FileUtils.writeByteArrayToFile(destFile, takeScreenShotAsByteArray());
            return destFile.getAbsolutePath();
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

}
