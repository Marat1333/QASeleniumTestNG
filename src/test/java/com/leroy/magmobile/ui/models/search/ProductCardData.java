package com.leroy.magmobile.ui.models.search;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductCardData extends CommonSearchCardData {

    private String barCode;
    private Double price;
    private Boolean hasAvailableStock;
    private Double availableQuantity;
    private String priceUnit;

    public ProductCardData() {
    }

    public ProductCardData(String lmCode) {
        setLmCode(lmCode);
    }

}

