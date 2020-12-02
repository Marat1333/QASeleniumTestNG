package com.leroy.magmobile.api.data.sales.orders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Data;

@Data
public class GiveAwayData {

    private String date;
    private Integer shopId;
    private String point;
    private String storageLocation;

    @JsonIgnore
    public LocalDateTime getDateAsLocalDateTime() {
        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime
                    .parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][.S]'Z'"));
        } catch (Exception ex) {
            localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        return localDateTime;
    }

    @JsonIgnore
    public void setDateAsLocalDateTime(LocalDateTime date) {
        this.date = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }
}
