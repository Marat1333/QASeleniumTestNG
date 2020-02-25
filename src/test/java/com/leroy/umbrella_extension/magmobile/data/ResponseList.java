package com.leroy.umbrella_extension.magmobile.data;


import java.util.List;

public class ResponseList {
    private List<?> items;

    public <T>T getItems() {
        return (T)items;
    }
}
