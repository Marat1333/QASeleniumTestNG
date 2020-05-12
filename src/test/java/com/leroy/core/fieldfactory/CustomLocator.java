package com.leroy.core.fieldfactory;

import org.openqa.selenium.By;

public class CustomLocator {

    private By by;
    private By parentBy;
    private String metaName;
    private boolean cacheLookup;
    private boolean refreshEveryTime;

    // Specific mobile selector options
    private String accessibilityId;

    public CustomLocator(String accessibilityId, String metaName) {
        this.accessibilityId = accessibilityId;
        this.metaName = metaName;
    }

    public CustomLocator(By by) {
        this.by = by;
    }

    public CustomLocator(By by, String metaName) {
        this(by);
        this.metaName = metaName;
    }

    public CustomLocator(By by, By parentBy, String metaName, boolean cacheLookup, boolean refreshEveryTime) {
        this(by);
        this.parentBy = parentBy;
        this.metaName = metaName;
        this.cacheLookup = cacheLookup;
        this.refreshEveryTime = refreshEveryTime;
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

    public boolean isRefreshEveryTime() {
        return refreshEveryTime;
    }

    public void setRefreshEveryTime(boolean refreshEveryTime) {
        this.refreshEveryTime = refreshEveryTime;
    }
}
