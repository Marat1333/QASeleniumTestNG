package com.leroy.core;

import com.leroy.core.configuration.Log;

import java.lang.reflect.Field;

public class BaseModel {

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

}
