package com.leroy.core.pages;

import com.leroy.core.TestContext;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;

public class ChromeCertificateErrorPage extends BaseWebPage {

    public ChromeCertificateErrorPage(TestContext context) {
        super(context);
    }

    private Element advancedBtn = new Element(driver, By.id("details-button"));
    private Element proceedLink = new Element(driver, By.id("proceed-link"));

    public void skipSiteSecureError() {
        if (advancedBtn.isVisible()) {
            advancedBtn.click();
            proceedLink.click();
        }
    }

}

