package com.leroy.core.pages;

import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EdgeCertificateErrorPage extends BaseWebPage {

    public EdgeCertificateErrorPage(WebDriver driver) {
        super(driver);
    }

    private Element detailsLink = new Element(driver, By.xpath("//a[@id='moreInformationDropdownLink']"));
    private Element goOnToTheWebpage = new Element(driver, By.xpath("//a[@id='overridelink']"));

    public void skipSiteSecureError() throws Exception {
        detailsLink.click();
        goOnToTheWebpage.click();
    }

}
