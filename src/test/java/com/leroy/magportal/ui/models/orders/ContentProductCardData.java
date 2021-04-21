package com.leroy.magportal.ui.models.orders;

import lombok.Data;

@Data
public class ContentProductCardData {
    private String lmCode;
    private String barCode;
    private String title;


    private Double createdQuantity;
    private Double orderedQuantity;
    private Double collectedQuantity;
    private Double givenAwayQuantity;
    private Double refundToClient;
    private Double toGiveAwayQuantity;
    private Double toDeliveryQuantity;

    public void increaseToGiveAwayQuantity(int val) { this.toGiveAwayQuantity += val;
    }

    public void decreaseToGiveAwayQuantity(int val) {
        this.toGiveAwayQuantity -= val;
    }

}
