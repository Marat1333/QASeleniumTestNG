package com.leroy.magportal.ui.pages.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MagPortalBasePage extends BaseWebPage {

    public MagPortalBasePage(TestContext context) {
        super(context);
        this.context = context;
    }

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

    protected boolean waitUntilContentHasChanged(String ps) {
        try {
            new WebDriverWait(driver, short_timeout).until(driver -> !getPageSource().equals(ps));
            return true;
        } catch (TimeoutException e) {
            Log.warn(String.format("waitForContentIsChanged failed (tried for %d second(s))", timeout));
            return false;
        }
    }

    /**
     * Is alert message with something error visible?
     */
    protected boolean isAlertErrorMessageVisible() {
        return alertErrorMessage.isVisible();
    }

}
