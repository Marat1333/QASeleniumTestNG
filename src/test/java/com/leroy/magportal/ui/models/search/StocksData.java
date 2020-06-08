package com.leroy.magportal.ui.models.search;

import lombok.Getter;

@Getter
public class StocksData {
    private Double availableForSale;
    private Integer unavailableForSale;
    private Integer saleHall;
    private Integer rm;
    private Integer em;
    private Integer rd;

    public StocksData(Double availableForSale, Integer unavailableForSale, Integer saleHall, Integer rm, Integer em, Integer rd) {
        this.availableForSale = availableForSale;
        this.unavailableForSale = unavailableForSale;
        this.saleHall = saleHall;
        this.rm = rm;
        this.em = em;
        this.rd = rd;
    }
}
