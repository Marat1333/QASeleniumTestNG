package com.leroy.magportal.ui.models.salesdoc;

import lombok.Data;

@Data
public class ShortSalesDocWebData implements IDataWithNumberAndStatus {

    private String number;
    private String status;
    private String author;
    private String creationDate;
    private Double totalPrice;

}
