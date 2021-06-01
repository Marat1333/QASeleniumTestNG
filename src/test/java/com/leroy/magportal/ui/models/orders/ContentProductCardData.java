package com.leroy.magportal.ui.models.orders;

import lombok.Data;

@Data
public class ContentProductCardData extends ToGiveAwayProductCardData {


    private Double toDeliveryQuantity;
    private String reasonForNonGiveaway = null;


    public void increaseCreatedQuantity(int val) { this.createdQuantity += val;
    }

    public void decreaseCreatedQuantity(int val) {
        this.createdQuantity -= val;
    }

}
