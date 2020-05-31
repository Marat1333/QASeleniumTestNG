package com.leroy.core.pages;

import com.leroy.core.BaseContainer;
import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.AssertWrapper;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.core.configuration.Log;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.Context;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;

public abstract class BasePage extends BaseContainer {

    protected StepLog log;
    protected SoftAssertWrapper softAssert;
    protected AssertWrapper anAssert;

    private static String screenshotPath = System.getProperty("output.path");

    protected BasePage(boolean isWaitForPageIsLoaded) {
        super(ContextProvider.getDriver());
        Context context = ContextProvider.getContext();
        this.log = context.getLog();
        this.softAssert = context.getSoftAssert();
        this.anAssert = context.getAnAssert();
        initElements();
        if (isWaitForPageIsLoaded)
            waitForPageIsLoaded();
    }

    public BasePage() {
        this(true);
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
