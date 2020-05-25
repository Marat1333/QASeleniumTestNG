package com.leroy.core.pages;

import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;

public class ChromeCertificateErrorPage extends BaseAppPage {

    // For NATIVE APP context
    private Element advancedBtn = new Element(driver, By.xpath("//*[@resource-id='details-button']"));
    //private Element proceedLink = new Element(driver, By.id("proceed-link"));
    private Element finalParagraph = new Element(driver, By.xpath("//*[@resource-id='final-paragraph']"));

    public void skipSiteSecureError() {
        if (advancedBtn.isVisible()) {
            advancedBtn.click();
            finalParagraph.click();
        }
    }

}

