package com.leroy.core.fieldfactory;

import org.openqa.selenium.By;

public class CustomLocator {

    private By by;
    private By parentBy;
    private String metaName;
    private boolean cacheLookup;

    // Specific mobile selector options
    private String accessibilityId;

    public CustomLocator(By by) {
        this.by = by;
    }

    public CustomLocator(By by, String metaName) {
        this(by);
        this.metaName = metaName;
    }

    public CustomLocator(By by, By parentBy, String metaName, boolean cacheLookup) {
        this(by);
        this.parentBy = parentBy;
        this.metaName = metaName;
        this.cacheLookup = cacheLookup;
    }

    public By getBy() {
        return by;
    }

    public By getParentBy() {
        return parentBy;
    }

    public String getMetaName() {
        return metaName;
    }

    public void setMetaName(String metaName) {
        this.metaName = metaName;
    }

    public String getAccessibilityId() {
        return accessibilityId;
    }

    public void setAccessibilityId(String accessibilityId) {
        this.accessibilityId = accessibilityId;
    }

    public boolean isCacheLookup() {
        return cacheLookup;
    }

    public void setCacheLookup(boolean cacheLookup) {
        this.cacheLookup = cacheLookup;
    }
}
