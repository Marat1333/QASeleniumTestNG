package com.leroy.magportal.ui.models.orders;

import lombok.Data;

@Data
public class ControlProductCardData {
    private String lmCode;
    private String barCode;
    private String title;


    private String reasonForNonGiveaway;
    private Double createdQuantity;
    private Double orderedQuantity;
    private Double collectedQuantity;
    private Double givenAwayQuantity;
    private Double refundToClient;
    private Double toGiveAwayQuantity;


}
