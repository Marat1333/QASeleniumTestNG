package com.leroy.core.fieldfactory;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.util.XpathUtil;
import org.openqa.selenium.By;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class CustomFieldElementLocator {

    private final Field field;
    private CustomLocator locator;

    public CustomFieldElementLocator(Field field, By parentBy) {
        this.field = field;
        this.locator = new CustomLocator(buildBy(), parentBy, buildMetaName());
        if (DriverFactory.isAppProfile())
            this.locator.setAccessibilityId(getAccessibilityId());
    }

    public CustomLocator getLocator() {
        return locator;
    }

    public By getBy() {
        return locator.getBy();
    }

    public String getMetaName() {
        return locator.getMetaName();
    }

    public String toString() {
        return this.getClass().getSimpleName() + " '" + this.locator.getBy() + "'";
    }

    public String getAccessibilityId() {
        return field.getAnnotation(AppFindBy.class).accessibilityId();
    }

    private By buildBy() {
        boolean isApp = DriverFactory.isAppProfile();
        Annotation annotation = isApp ? field.getAnnotation(AppFindBy.class) : field.getAnnotation(WebFindBy.class);
        if (annotation == null)
            return null;
        String id = isApp ? field.getAnnotation(AppFindBy.class).id() : field.getAnnotation(WebFindBy.class).id();
        String xpath = isApp ?
                field.getAnnotation(AppFindBy.class).xpath() : field.getAnnotation(WebFindBy.class).xpath();
        if (!id.isEmpty())
            return By.id(id);
        if (!xpath.isEmpty()) {
            if (xpath.startsWith("./")) {
                return By.xpath(XpathUtil.getXpath(locator.getParentBy()) + xpath.replaceFirst(".", ""));
            }
            return By.xpath(xpath);
        }
        if (isApp)
            return null;
        else
            throw new IllegalArgumentException("If you use a '@WebFindBy' annotation, " +
                    "you must specify either a id or xpath or something else");
    }

    private String buildMetaName() {
        Annotation annotation = DriverFactory.isAppProfile() ?
                field.getAnnotation(AppFindBy.class) : field.getAnnotation(WebFindBy.class);
        if (annotation == null)
            return null;
        String metaName = DriverFactory.isAppProfile() ?
                field.getAnnotation(AppFindBy.class).metaName() :
                field.getAnnotation(WebFindBy.class).metaName();
        if (!metaName.isEmpty())
            return metaName;
        return field.getName();
    }
}

