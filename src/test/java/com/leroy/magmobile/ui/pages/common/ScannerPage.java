package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import io.qameta.allure.Step;

public class ScannerPage extends CommonMagMobilePage{
    @AppFindBy(text = "РАЗРЕШИТЬ")
    Button allowAccessToCameraBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenContent\"]/android.view.ViewGroup[2]")
    Button closeScannerBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenContent\"]/android.view.ViewGroup[3]")
    Button flashLightBtn;

    @Step("Закрыть сканер")
    public void closeScanner(){
        closeScannerBtn.click();
        closeScannerBtn.waitForInvisibility(short_timeout);
        if (closeScannerBtn.isVisible()){
            closeScannerBtn.click();
        }
    }

    @Override
    protected void waitForPageIsLoaded() {
        if (allowAccessToCameraBtn.isVisible()){
            allowAccessToCameraBtn.click();
            allowAccessToCameraBtn.waitForInvisibility();
        }
        closeScannerBtn.waitForVisibility();
        flashLightBtn.waitForVisibility();
    }

    protected void verifyRequiredElements(){
        softAssert.areElementsVisible(closeScannerBtn, flashLightBtn);
        softAssert.verifyAll();
    }
}
