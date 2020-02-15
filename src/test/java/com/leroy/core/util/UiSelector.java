package com.leroy.core.util;

public class UiSelector {

    private boolean checkable;

    public UiSelector checkable(boolean val) {
        this.checkable = val;
        return this;
    }

    private boolean checked;

    public UiSelector checked(boolean val) {
        this.checked = val;
        return this;
    }

    private UiSelector childSelector;

    public UiSelector childSelector(UiSelector selector) {
        this.childSelector = selector;
        return this;
    }

    private String className;

    public UiSelector className(String className) {
        this.className = className;
        return this;
    }

    private String classNameMatches;

    public UiSelector classNameMatches(String regex) {
        this.classNameMatches = regex;
        return this;
    }

    private boolean clickable;

    public UiSelector clickable(boolean val) {
        this.clickable = val;
        return this;
    }

    private String description;

    public UiSelector description(String desc) {
        this.description = desc;
        return this;
    }

    private String descriptionContains;

    public UiSelector descriptionContains(String desc) {
        this.descriptionContains = desc;
        return this;
    }

    private String descriptionMatches;

    public UiSelector descriptionMatches(String regex) {
        this.descriptionMatches = regex;
        return this;
    }

    private String descriptionStartsWith;

    public UiSelector descriptionStartsWith(String desc) {
        this.descriptionStartsWith = desc;
        return this;
    }

    private boolean enabled;

    public UiSelector enabled(boolean val) {
        this.enabled = val;
        return this;
    }

    private boolean focusable;

    public UiSelector focusable(boolean val) {
        this.focusable = val;
        return this;
    }

    private boolean focused;

    public UiSelector focused(boolean val) {
        this.focused = val;
        return this;
    }

    private UiSelector fromParent;

    public UiSelector fromParent(UiSelector selector) {
        this.fromParent = selector;
        return this;
    }

    private int index;

    public UiSelector index(int index) {
        this.index = index;
        return this;
    }

    private int instance;

    public UiSelector instance(int instance) {
        this.instance = instance;
        return this;
    }

    private boolean longClickable;

    public UiSelector longClickable(boolean val) {
        this.longClickable = val;
        return this;
    }

    private String packageName;

    public UiSelector packageName(String name) {
        this.packageName = name;
        return this;
    }

    private String packageNameMatches;

    public UiSelector packageNameMatches(String regex) {
        this.packageNameMatches = regex;
        return this;
    }

    private String resourceId;

    public UiSelector resourceId(String id) {
        this.resourceId = id;
        return this;
    }

    private String resourceIdMatches;

    public UiSelector resourceIdMatches(String regex) {
        this.resourceIdMatches = regex;
        return this;
    }

    private boolean scrollable;

    public UiSelector scrollable(boolean val) {
        this.scrollable = val;
        return this;
    }

    private boolean selected;

    public UiSelector selected(boolean val) {
        this.selected = val;
        return this;
    }

    private String text;

    public UiSelector text(String text) {
        this.text = text;
        return this;
    }

    private String textContains;

    public UiSelector textContains(String text) {
        this.textContains = text;
        return this;
    }

    private String textMatches;

    public UiSelector textMatches(String regex) {
        this.textMatches = regex;
        return this;
    }

    private String textStartsWith;

    public UiSelector textStartsWith(String text) {
        this.textStartsWith = text;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("new UiSelector()");
        if (description != null)
            result.append(".description(").append(description).append(")");
        if (className != null) {
            result.append(".className(").append(className).append(")");
        }
        return result.toString();
    }

}
