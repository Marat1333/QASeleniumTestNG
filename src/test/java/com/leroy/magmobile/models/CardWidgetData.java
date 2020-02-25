package com.leroy.magmobile.models;

import com.leroy.core.BaseModel;

public abstract class CardWidgetData extends BaseModel {

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

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof CardWidgetData)) {
            return false;
        }

        // typecast o to CardWidgetData so that we can compare data members
        CardWidgetData c = (CardWidgetData) o;

        // Compare the data members and return accordingly
        return toString().equals(c.toString());
    }
}
