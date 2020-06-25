package com.leroy.magmobile.api.data.sales.orders;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class GiveAwayData {

    private String date;
    private Integer shopId;
    private String point;

    public LocalDateTime getDateAsLocalDateTime() {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]'Z'"));
    }

    public void setDate(LocalDateTime date) {
        this.date = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

}
