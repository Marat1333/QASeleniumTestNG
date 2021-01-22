package com.leroy.magmobile.ui.pages.work.ruptures.data;

import lombok.Data;

import java.util.Objects;

@Data
public class SessionData {
    private String sessionNumber;
    private String createDate;
    private int ruptureQuantity;
    private String creatorName;
    private String type;

    //из-за различных источников данных (бэк и фронт) решили ограничится несколькими полями
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionData)) return false;
        SessionData that = (SessionData) o;
        return ruptureQuantity == that.ruptureQuantity &&
                Objects.equals(sessionNumber, that.sessionNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionNumber, ruptureQuantity);
    }
}
