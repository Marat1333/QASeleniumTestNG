package com.leroy.core.pages;

import com.leroy.core.BaseContainer;
import com.leroy.core.TestContext;
import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.web_elements.general.Element;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;

public class BasePage extends BaseContainer {

    protected StepLog log;
    protected CustomSoftAssert softAssert;
    protected CustomAssert anAssert;
    protected TestContext context;

    public BasePage(TestContext context) {
        super(context.getDriver());
        this.log = context.getLog();
        this.softAssert = context.getSoftAssert();
        this.anAssert = context.getAnAssert();
        this.context = context;
        initElements();
        waitForPageIsLoaded();
    }

    public void waitForPageIsLoaded() {
    }

    public BasePage verifyRequiredElements() throws Exception {
        return this;
    }

    public Element E(String str, String metaName) {
        if (str.startsWith("/"))
            return new Element(driver, new CustomLocator(By.xpath(str), metaName));
        else if (str.startsWith("#"))
            return new Element(driver, new CustomLocator(
                    By.id(str.replaceFirst("#", ""))), metaName);
        else if (str.startsWith("$")) {
            CustomLocator locator = new CustomLocator(str.replaceFirst("\\$", ""), metaName);
            return new Element(driver, locator);
        } else if (str.contains("contains(")) {
            String _xpathTmp = DriverFactory.isAppProfile() ? "//*[contains(@text, '%s')]" : "//*[contains(.,'%s')]";
            String subStr = StringUtils.substringBetween(str, "contains(", ")");
            return new Element(driver, new CustomLocator(By.xpath(String.format(_xpathTmp, subStr)),
                    metaName == null ? String.format("Элемент содержащий текст '%s'", subStr) : metaName));
        } else {
            String _xpathTmp = DriverFactory.isAppProfile() ? "//*[@text='%s']" : "//*[text()='%s']";
            return new Element(driver, new CustomLocator(By.xpath(String.format(_xpathTmp, str)),
                    metaName == null ? String.format("Элемент с текстом '%s'", str) : metaName));
        }
    }

    public Element E(String str) {
        return E(str, null);
    }

}
