package com.leroy.models;

import com.leroy.core.configuration.Log;

import java.lang.reflect.Field;

public abstract class CardWidgetData {

    private String toJsonString() throws IllegalAccessException {
        StringBuilder result = new StringBuilder("{\n");
        Class<?> current = this.getClass();
        while (current.getSuperclass() != null) {
            for (Field field : current.getDeclaredFields()) {
                field.setAccessible(true);
                boolean isStringFld = field.getType().getName().equals("java.lang.String");
                Object fldValue = field.get(this);
                if (fldValue != null) {
                    if (result.length() > 3)
                        result.append(", ");
                    result.append("\"")
                            .append(field.getName())
                            .append("\": ");
                    if (isStringFld)
                        result.append("\"");
                    result.append(fldValue.toString());
                    if (isStringFld)
                        result.append("\"");
                }
            }
            current = current.getSuperclass();
        }
        result.append("\n}");
        return result.toString();
    }

    @Override
    public String toString() {
        try {
            return toJsonString();
        } catch (IllegalAccessException err) {
            Log.error("toString() method. Error: " + err.getMessage());
            return null;
        }
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
