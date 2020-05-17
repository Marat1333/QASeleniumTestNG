package com.leroy.magportal.ui.models.salesdoc;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortOrderDocWebData implements IDataWithNumberAndStatus {
    private String number;
    private String status;
    private String customer;
    private LocalDateTime creationDate;
    private Double totalPrice;
    private String deliveryType;
}
