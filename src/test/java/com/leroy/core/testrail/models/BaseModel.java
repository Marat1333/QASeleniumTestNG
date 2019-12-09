package com.leroy.core.testrail.models;

import com.leroy.core.configuration.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BaseModel {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    protected Map<String, Object> getData() {
        return new HashMap<>();
    }

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
