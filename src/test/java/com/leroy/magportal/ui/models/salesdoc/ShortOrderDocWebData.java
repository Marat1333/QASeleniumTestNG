package com.leroy.magportal.ui.models.salesdoc;

import lombok.Data;

@Data
public class ShortOrderDocWebData implements IDataWithNumberAndStatus {
    private String number;
    private String status;
    private String customer;
    private String creationDate;
    private Double totalPrice;
    private String deliveryType;
}
