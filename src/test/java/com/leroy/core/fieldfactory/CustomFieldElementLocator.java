package com.leroy.core.fieldfactory;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.util.XpathUtil;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.elements.MagMobWhiteSubmitButton;
import org.openqa.selenium.By;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class CustomFieldElementLocator {

    private final Field field;
    private CustomLocator locator;

    public CustomFieldElementLocator(Field field, By parentBy) {
        this.field = field;
        this.locator = new CustomLocator(buildBy(parentBy), parentBy,
                buildMetaName(), isNeedToCacheLookupField(), isNeedToRefreshEveryTime());
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
        if (field.getAnnotation(AppFindBy.class) != null)
            return field.getAnnotation(AppFindBy.class).accessibilityId();
        else return null;
    }

    private By buildBy(By parentBy) {
        String parentXpath = parentBy == null ? "" : XpathUtil.getXpath(parentBy);
        boolean isApp = DriverFactory.isAppProfile();
        Annotation annotation = isApp ? field.getAnnotation(AppFindBy.class) : field.getAnnotation(WebFindBy.class);
        if (annotation == null)
            return null;
        String id = isApp ? field.getAnnotation(AppFindBy.class).id() : field.getAnnotation(WebFindBy.class).id();
        String xpath = isApp ?
                field.getAnnotation(AppFindBy.class).xpath() : field.getAnnotation(WebFindBy.class).xpath();
        String text = isApp ?
                field.getAnnotation(AppFindBy.class).text() : field.getAnnotation(WebFindBy.class).text();
        String containsText = isApp ?
                field.getAnnotation(AppFindBy.class).containsText() : field.getAnnotation(WebFindBy.class).containsText();


        String followingTextAfter = isApp ? field.getAnnotation(AppFindBy.class).followingTextAfter() : "";
        if (!id.isEmpty())
            return By.id(id);
        if (!xpath.isEmpty()) {
            if (xpath.startsWith("./")) {
                return By.xpath(parentXpath + xpath.replaceFirst(".", ""));
            }
            return By.xpath(xpath);
        }
        if (!text.isEmpty()) {
            if (field.getType().equals(MagMobGreenSubmitButton.class) ||
                    field.getType().equals(MagMobWhiteSubmitButton.class) ||
                    field.getType().equals(MagMobButton.class))
                return By.xpath(parentXpath + "//android.view.ViewGroup[android.widget.TextView[@text='" + text + "']]");
            if (isApp)
                return By.xpath(parentXpath + "//*[@text='" + text + "']");
            else
                return By.xpath(parentXpath + "//*[text()='" + text + "']");
        }
        if (!containsText.isEmpty()) {
            if (field.getType().equals(MagMobGreenSubmitButton.class) ||
                    field.getType().equals(MagMobWhiteSubmitButton.class) ||
                    field.getType().equals(MagMobButton.class))
                return By.xpath(parentXpath + "//android.view.ViewGroup[android.widget.TextView[contains(@text, '" + containsText + "')]]");
            if (isApp)
                return By.xpath(parentXpath + "//*[contains(@text, '" + containsText + "')]");
            else
                return By.xpath(parentXpath + "//*[contains(text(), '" + containsText + "')]");
        }
        if (!followingTextAfter.isEmpty()) {
            return By.xpath(parentXpath + "//android.widget.TextView[@text='" + followingTextAfter + "']/following-sibling::android.widget.TextView");
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
            String containsTextElem = field.getAnnotation(AppFindBy.class).containsText();
            if (!containsTextElem.isEmpty())
                return "Элемент содержащий текст '" + containsTextElem + "'";
            String followingTextElem = field.getAnnotation(AppFindBy.class).followingTextAfter();
            if (!followingTextElem.isEmpty())
                return followingTextElem;
        }
        return field.getName();
    }

    private boolean isNeedToCacheLookupField() {
        return DriverFactory.isAppProfile() ?
                field.getAnnotation(AppFindBy.class).cacheLookup() :
                field.getAnnotation(WebFindBy.class).cacheLookup();
    }

    private boolean isNeedToRefreshEveryTime() {
        return !DriverFactory.isAppProfile() && field.getAnnotation(WebFindBy.class).refreshEveryTime();
    }
}

