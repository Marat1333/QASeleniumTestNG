package com.leroy.magportal.ui.models.search;

import lombok.Getter;

@Getter
public class PriceContainerData {
    private String price;
    private String currency;
    private String units;

    public PriceContainerData(String price, String currency, String units){
        this.currency=currency;
        this.price=price;
        this.units=units;
    }

}
