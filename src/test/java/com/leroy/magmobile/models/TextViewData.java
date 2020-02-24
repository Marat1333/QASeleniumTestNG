package com.leroy.magmobile.models;

import com.leroy.magmobile.models.CardWidgetData;

public class TextViewData extends CardWidgetData {

    private String text;

    public TextViewData(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return getText();
    }
}
