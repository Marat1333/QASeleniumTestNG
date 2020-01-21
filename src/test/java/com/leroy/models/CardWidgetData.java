package com.leroy.models;

import com.leroy.core.BaseModel;

public abstract class CardWidgetData extends BaseModel {

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
