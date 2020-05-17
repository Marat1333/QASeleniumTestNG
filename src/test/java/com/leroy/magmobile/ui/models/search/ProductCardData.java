package com.leroy.magmobile.ui.models.search;

import com.leroy.magmobile.ui.models.CardWidgetData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class ProductCardData extends CardWidgetData {

    private String lmCode;
    private String barCode;
    private String name;
    private Double price;
    private Boolean hasAvailableStock;
    private Double availableQuantity;
    private String priceUnit;

    public ProductCardData() {
    }

    public ProductCardData(String lmCode) {
        this.lmCode = lmCode;
    }

    public void addAvailableQuantity(Double availableQuantity) {
        this.availableQuantity += availableQuantity;
    }

    public boolean compareOnlyNotNullFields(ProductCardData cardData) {
        if (cardData == null)
            return false;
        return lmCode.equals(cardData.getLmCode()) &&
                equalsIfRightNotNull(barCode, cardData.getBarCode()) &&
                equalsIfRightNotNull(name, cardData.getName()) &&
                equalsIfRightNotNull(price, cardData.getPrice()) &&
                equalsIfRightNotNull(hasAvailableStock, cardData.getHasAvailableStock()) &&
                equalsIfRightNotNull(availableQuantity, cardData.getAvailableQuantity()) &&
                equalsIfRightNotNull(priceUnit, cardData.getPriceUnit());
    }

}

