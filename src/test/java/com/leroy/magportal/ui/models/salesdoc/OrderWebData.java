package com.leroy.magportal.ui.models.salesdoc;

import lombok.Data;

import java.util.List;

@Data
public class OrderWebData {

    private List<ProductOrderCardWebData> productCardDataList;
    private Integer productCount;
    private Double totalWeight; // кг
    private Double totalPrice; // Рубли

}
