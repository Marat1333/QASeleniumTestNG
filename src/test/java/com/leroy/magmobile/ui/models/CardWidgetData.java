package com.leroy.magmobile.ui.models;

public abstract class CardWidgetData {

    protected boolean equalsIfLeftNotNull(Object arg1, Object arg2) {
        if (arg1 != null)
            return arg1.equals(arg2);
        else
            return true;
    }

    protected boolean equalsIfRightNotNull(Object arg1, Object arg2) {
        if (arg2 != null)
            return arg2.equals(arg1);
        else
            return true;
    }
}
