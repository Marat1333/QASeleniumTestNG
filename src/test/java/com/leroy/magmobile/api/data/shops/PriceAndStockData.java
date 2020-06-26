package com.leroy.magmobile.api.data.shops;

import lombok.Getter;

@Getter
public class PriceAndStockData {
    public PriceAndStockData(Double price, Double stock){
        this.price = price;
        this.stock = stock;
    }
    private Double price;
    private Double stock;
}
