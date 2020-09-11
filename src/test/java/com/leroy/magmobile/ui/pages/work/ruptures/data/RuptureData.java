package com.leroy.magmobile.ui.pages.work.ruptures.data;

import lombok.Data;

import java.util.Map;
import java.util.Objects;

@Data
public class RuptureData {
    private String lmCode;
    private String barCode;
    private String title;
    private Map<String, Boolean> actions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuptureData)) return false;
        RuptureData that = (RuptureData) o;
        return Objects.equals(lmCode, that.lmCode) &&
                Objects.equals(barCode, that.barCode) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lmCode, barCode, title);
    }
}
