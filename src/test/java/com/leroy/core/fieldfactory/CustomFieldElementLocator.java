package com.leroy.core.fieldfactory;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.util.XpathUtil;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import org.openqa.selenium.By;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class CustomFieldElementLocator {

    private final Field field;
    private CustomLocator locator;

    public CustomFieldElementLocator(Field field, By parentBy) {
        this.field = field;
        this.locator = new CustomLocator(buildBy(parentBy), parentBy, buildMetaName(), isNeedToCacheLookupField());
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

    private By buildBy(By parentBy) {
        boolean isApp = DriverFactory.isAppProfile();
        Annotation annotation = isApp ? field.getAnnotation(AppFindBy.class) : field.getAnnotation(WebFindBy.class);
        if (annotation == null)
            return null;
        String id = isApp ? field.getAnnotation(AppFindBy.class).id() : field.getAnnotation(WebFindBy.class).id();
        String xpath = isApp ?
                field.getAnnotation(AppFindBy.class).xpath() : field.getAnnotation(WebFindBy.class).xpath();
        String text = isApp ?
                field.getAnnotation(AppFindBy.class).text() : field.getAnnotation(WebFindBy.class).text();
        if (!id.isEmpty())
            return By.id(id);
        if (!xpath.isEmpty()) {
            if (xpath.startsWith("./")) {
                return By.xpath(XpathUtil.getXpath(parentBy) + xpath.replaceFirst(".", ""));
            }
            return By.xpath(xpath);
        }
        if (!text.isEmpty()) {
            if (field.getType().equals(MagMobSubmitButton.class) || field.getType().equals(MagMobButton.class))
                return By.xpath("//android.view.ViewGroup[android.widget.TextView[@text='" + text + "']]");
            if (isApp)
                return By.xpath("//*[@text='" + text + "']");
            else
                return By.xpath("//*[text()='" + text + "']");
        }
        if (isApp)
            return null;
        else
            throw new IllegalArgumentException("If you use a '@WebFindBy' annotation, " +
                    "you must specify either a id or xpath or something else");
    }

    private String buildMetaName() {
        boolean isApp = DriverFactory.isAppProfile();
        Annotation annotation = isApp ?
                field.getAnnotation(AppFindBy.class) : field.getAnnotation(WebFindBy.class);
        if (annotation == null)
            return null;
        String metaName = isApp ?
                field.getAnnotation(AppFindBy.class).metaName() :
                field.getAnnotation(WebFindBy.class).metaName();
        if (!metaName.isEmpty())
            return metaName;
        if (isApp) {
            String textElem = field.getAnnotation(AppFindBy.class).text();
            if (!textElem.isEmpty())
                return "Элемент с текстом '" + textElem + "'";
        }
        return field.getName();
    }

    private boolean isNeedToCacheLookupField() {
        return DriverFactory.isAppProfile() ?
                field.getAnnotation(AppFindBy.class).cacheLookup() :
                field.getAnnotation(WebFindBy.class).cacheLookup();
    }
}

