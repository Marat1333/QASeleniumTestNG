package com.leroy.magmobile.api.data.sales.cart_estimate.cart;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartDiscountData {
    private String type;
    private Integer typeValue;
    private String actor;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updated;
    private CartDiscountReasonData reason;
}
