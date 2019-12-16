package com.leroy.core.pages;

import com.leroy.core.BaseContainer;
import com.leroy.core.TestContext;
import com.leroy.core.configuration.CustomAssert;
import com.leroy.core.configuration.CustomSoftAssert;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.web_elements.general.Element;
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

    public Element getElementByText(String text) {
        return new Element(driver, new CustomLocator(By.xpath(String.format("//*[@text='%s']", text)), null,
                String.format("Элемент с текстом '%s'", text), false));
    }

}
