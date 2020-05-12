package com.leroy.core.web_elements.general;

import com.leroy.core.BaseContainer;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.util.XpathUtil;
import org.openqa.selenium.WebDriver;

public abstract class BaseWrapper extends BaseContainer {

    protected CustomLocator locator;

    public BaseWrapper(WebDriver driver) {
        super(driver);
    }

    public BaseWrapper(WebDriver driver, CustomLocator locator) {
        super(driver);
        this.locator = locator;
        initElements(locator);
    }

    public boolean isCacheLookup() {
        return locator.isCacheLookup();
    }

    public boolean isRefreshEveryTime() {
        return locator.isRefreshEveryTime();
    }

    /**
     * Get xpath of the element
     *
     * @return String
     */
    public String getXpath() {
        if (locator.getBy() != null)
            return XpathUtil.getXpath(locator.getBy());
        else if (locator.getAccessibilityId() != null)
            return XpathUtil.getXpathByAccessibilityId(locator.getAccessibilityId());
        else
            return null;
    }

    /**
     * Get name of Element
     */
    public String getMetaName() {
        return locator.getMetaName();
    }

    /**
     * Set the name of the Element
     *
     * @param metaName
     */
    protected void setMetaName(String metaName) {
        this.locator.setMetaName(metaName);
    }

}
