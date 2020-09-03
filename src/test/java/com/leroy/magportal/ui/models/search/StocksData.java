package com.leroy.magportal.ui.models.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StocksData {

    private Double availableForSale;
    private Integer unavailableForSale;
    private Integer saleHall;
    private Integer rm;
    private Integer em;
    private Integer rd;
}
