package com.leroy.magmobile.ui.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class TextViewData extends CardWidgetData {

    private String text;

    public TextViewData(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return getText();
    }
}
