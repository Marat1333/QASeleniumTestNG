package com.leroy.magportal.ui.models.orders;

import lombok.Data;

@Data
public class ToGiveAwayProductCardData {
    protected String lmCode;
    protected String barCode;
    protected String title;


    protected String reasonForNonGiveaway;
    protected Double createdQuantity;
    protected Double orderedQuantity;
    protected Double collectedQuantity;
    protected Double givenAwayQuantity;
    protected Double refundToClient;
    protected Double toGiveAwayQuantity;

    public void increaseToGiveAwayQuantity(Double val) { this.toGiveAwayQuantity += val;
    }

    public void decreaseToGiveAwayQuantity(Double val) {
        this.toGiveAwayQuantity -= val;
    }

}
