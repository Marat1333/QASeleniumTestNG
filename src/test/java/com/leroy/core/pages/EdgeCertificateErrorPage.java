package com.leroy.core.pages;

import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;

public class EdgeCertificateErrorPage extends BaseWebPage {

    private Element detailsLink = new Element(driver, By.xpath("//a[@id='moreInformationDropdownLink']"));
    private Element goOnToTheWebpage = new Element(driver, By.xpath("//a[@id='overridelink']"));

    public void skipSiteSecureError() throws Exception {
        detailsLink.click();
        goOnToTheWebpage.click();
    }

}
