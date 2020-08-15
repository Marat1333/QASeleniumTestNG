package com.leroy.magportal.ui.pages.common;

import com.leroy.core.Context;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.NewFeaturesModalWindow;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;

public class MagPortalBasePage extends BaseWebPage {

    public MagPortalBasePage() {
        super(By.id("ClientOrdersApp"));
    }

    protected Context context;

    @WebFindBy(xpath = "//div[contains(@class, 'Spinner')]")
    private Element spinnerIcon;

    @WebFindBy(xpath = "//div[@role='alert' and contains(@class, 'Toastify')]")
    private Element alertErrorMessage;

    @Step("Подождать появления окна с новыми фичами и закрыть его, если оно появится")
    public MagPortalBasePage closeNewFeaturesModalWindowIfExist() {
        NewFeaturesModalWindow modalWindow = new NewFeaturesModalWindow(driver);
        if (modalWindow.isVisible(short_timeout))
            modalWindow.clickSubmitButton();
        modalWindow.waitForInvisibility();
        return this;
    }

    protected void waitForSpinnerAppearAndDisappear(int timeout) {
        try {
            spinnerIcon.waitForVisibility(timeout, Duration.ofMillis(100));
        } catch (TimeoutException err) {
            // nothing to do
        }
        spinnerIcon.waitForInvisibility();
    }

    protected void waitForSpinnerAppearAndDisappear() {
        waitForSpinnerAppearAndDisappear(tiny_timeout);
    }

    protected void waitForSpinnerDisappear() {
        spinnerIcon.waitForInvisibility();
    }

    /**
     * Is alert message with something error visible?
     */
    protected boolean isAlertErrorMessageVisible() {
        return alertErrorMessage.isVisible();
    }

}
