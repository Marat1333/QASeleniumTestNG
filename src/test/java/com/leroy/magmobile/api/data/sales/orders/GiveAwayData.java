package com.leroy.magmobile.api.data.sales.orders;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GiveAwayData {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SS]'Z'")
    private LocalDateTime date;
    private Integer shopId;
    private String point;

}
