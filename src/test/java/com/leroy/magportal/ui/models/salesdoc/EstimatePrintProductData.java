package com.leroy.magportal.ui.models.salesdoc;

import lombok.Data;

@Data
public class EstimatePrintProductData {

    private String lmCode;
    private String title;
    private Double quantity;
    private Double percentNDS;
    private Double price;
    private Double totalPriceWithNDS;
}
