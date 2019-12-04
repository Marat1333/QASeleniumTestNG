package com.leroy.core.fieldfactory;

import org.openqa.selenium.By;

public class CustomLocator {

    private By by;
    private By parentBy;
    private String metaName;

    public CustomLocator(By by) {
        this.by = by;
    }

    public CustomLocator(By by, By parentBy, String metaName) {
        this(by);
        this.parentBy = parentBy;
        this.metaName = metaName;
    }

    // Specific mobile selector options
    private String accessibilityId;

    public By getBy() {
        return by;
    }

    public By getParentBy() {
        return parentBy;
    }

    public String getMetaName() {
        return metaName;
    }

    public String getAccessibilityId() {
        return accessibilityId;
    }

    public void setAccessibilityId(String accessibilityId) {
        this.accessibilityId = accessibilityId;
    }
}
