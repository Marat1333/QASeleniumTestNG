package com.leroy.magportal.ui.models.orders;

import lombok.Data;

@Data
public class ContentProductCardData extends ToGiveAwayProductCardData {


    private Double toDeliveryQuantity;
    private String reasonForNonGiveaway = null;


    public void increaseCreatedQuantity(Double val) { this.createdQuantity += val;
    }

    public void decreaseCreatedQuantity(Double val) {
        this.createdQuantity -= val;
    }

}
