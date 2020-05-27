package com.leroy.magmobile.ui.models.sales;

import com.leroy.magmobile.ui.models.CardWidgetData;
import com.leroy.magmobile.ui.models.search.ProductCardData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class SalesOrderCardData extends CardWidgetData {

    private ProductCardData productCardData;
    private Double selectedQuantity;
    private Double totalPrice;
    private Integer availableTodayQuantity;
    private boolean negativeAvailableQuantity;

    public boolean compareOnlyNotNullFields(SalesOrderCardData orderCardData) {
        if (orderCardData == null || orderCardData.getProductCardData() == null)
            return false;
        return getProductCardData().compareOnlyNotNullFields(orderCardData.getProductCardData()) &&
                equalsIfRightNotNull(selectedQuantity, orderCardData.getSelectedQuantity()) &&
                equalsIfRightNotNull(totalPrice, orderCardData.getTotalPrice()) &&
                equalsIfRightNotNull(availableTodayQuantity, orderCardData.getAvailableTodayQuantity());
    }

}
