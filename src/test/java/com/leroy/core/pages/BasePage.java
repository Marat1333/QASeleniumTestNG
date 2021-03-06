package com.leroy.core.pages;

import com.leroy.core.BaseContainer;
import com.leroy.core.Context;
import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.AssertWrapper;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.core.configuration.Log;
import com.leroy.core.testrail.helpers.StepLog;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;

public abstract class BasePage extends BaseContainer {

    protected StepLog log;
    protected SoftAssertWrapper softAssert;
    protected AssertWrapper anAssert;

    private static String screenshotPath = System.getProperty("output.path");

    protected BasePage(By frameLocator, boolean isWaitForPageIsLoaded) {
        super(ContextProvider.getDriver());
        Context context = ContextProvider.getContext();
        this.log = context.getLog();
        this.softAssert = context.getSoftAssert();
        this.anAssert = context.getAnAssert();
        initElements();
        if (frameLocator != null) {
            driver.switchTo().defaultContent();
            new WebDriverWait(driver, short_timeout).until(
                    ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
        }
        if (isWaitForPageIsLoaded)
            waitForPageIsLoaded();
    }

    public BasePage(By frameLocator) {
        this(frameLocator, true);
    }

    public BasePage() {
        this(null, true);
    }

    protected void waitForPageIsLoaded() {
    }

    @Step("Перейти назад")
    public boolean navigateBack() throws InterruptedException {
        try {
            this.driver.navigate().back();
            return true;
        } catch (Exception var2) {
            Log.error("Method: navigateBack");
            Log.error("Error: There was a problem navigating back on the browser history");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    @Step("Перейти вперед")
    public boolean navigateForward() throws InterruptedException {
        try {
            this.driver.navigate().forward();
            return true;
        } catch (Exception var2) {
            Log.error("Method: navigateForward");
            Log.error("Error: There was a problem navigating forward on the browser history");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
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
