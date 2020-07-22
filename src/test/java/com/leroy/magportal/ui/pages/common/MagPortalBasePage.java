package com.leroy.magportal.ui.pages.common;

import com.leroy.core.Context;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;

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

    protected void waitForSpinnerAppearAndDisappear(int timeout) {
        spinnerIcon.waitForVisibility(timeout, Duration.ofMillis(100));
        spinnerIcon.waitForInvisibility();
    }

    protected void waitForSpinnerAppearAndDisappear() {
        spinnerIcon.waitForVisibility(tiny_timeout, Duration.ofMillis(100));
        spinnerIcon.waitForInvisibility();
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
