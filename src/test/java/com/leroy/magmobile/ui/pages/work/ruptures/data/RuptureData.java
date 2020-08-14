package com.leroy.magmobile.ui.pages.work.ruptures.data;

import lombok.Data;

import java.util.Map;

@Data
public class RuptureData {
    private String lmCode;
    private String barCode;
    private String title;
    private Map<String, Boolean> actions;
}
