package com.leroy.core.pages;

import com.leroy.core.BaseContainer;
import com.leroy.core.TestContext;
import com.leroy.core.asserts.CustomAssert;
import com.leroy.core.asserts.CustomSoftAssert;
import com.leroy.core.configuration.Log;
import com.leroy.core.testrail.helpers.StepLog;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;

public abstract class BasePage extends BaseContainer {

    protected TestContext context;
    protected StepLog log;
    protected CustomSoftAssert softAssert;
    protected CustomAssert anAssert;

    private static String screenshotPath = System.getProperty("output.path");

    protected BasePage(TestContext context, boolean isWaitForPageIsLoaded) {
        super(context.getDriver());
        this.log = context.getLog();
        this.softAssert = context.getSoftAssert();
        this.anAssert = context.getAnAssert();
        initContext(context);
        initElements();
        if (isWaitForPageIsLoaded)
            waitForPageIsLoaded();
    }

    public BasePage(TestContext context) {
        this(context, true);
    }

    public void initContext(TestContext context) {
        this.context = context;
    }

    public void waitForPageIsLoaded() {
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    protected byte[] takeScreenShotAsByteArray() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public String takeScreenShot(String fileName) throws IOException {
        try {
            Log.info("Screen shot FileName: " + fileName);
            File destFile = new File(screenshotPath + File.separator + fileName);
            FileUtils.writeByteArrayToFile(destFile, takeScreenShotAsByteArray());
            return destFile.getAbsolutePath();
        } catch (Exception err) {
            Log.error(err.getMessage());
            throw err;
        }
    }

}
