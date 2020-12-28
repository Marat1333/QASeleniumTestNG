package com.leroy.magportal.ui.models.orders;

import lombok.Data;

@Data
public class ControlProductCardData {
    private String lmCode;
    private String barCode;
    private String title;


    private String reasonForNonGiveaway;
    private Integer createdQuantity;
    private Integer orderedQuantity;
    private Integer collectedQuantity;
    private Integer givenAwayQuantity;
    private Integer refundToClient;
    private Integer toGiveAwayQuantity;


    /*public void increaseToGiveAwayQuantity(int val) { this.toGiveAwayQuantity += val;
    }

    public void decreaseToGiveAwayQuantity(int val) {
        this.toGiveAwayQuantity -= val;
    }*/

}
