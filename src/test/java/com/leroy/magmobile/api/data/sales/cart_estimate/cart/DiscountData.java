package com.leroy.magmobile.api.data.sales.cart_estimate.cart;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.leroy.magmobile.api.data.sales.DiscountReasonData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiscountData {
    private String type;
    private Integer typeValue;
    private String actor;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    private LocalDateTime updated;
    private DiscountReasonData reason;
}
