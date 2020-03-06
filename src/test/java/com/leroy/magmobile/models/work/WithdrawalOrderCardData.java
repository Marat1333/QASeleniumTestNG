package com.leroy.magmobile.models.work;

import com.leroy.magmobile.models.CardWidgetData;
import com.leroy.magmobile.models.search.ProductCardData;

public class WithdrawalOrderCardData extends CardWidgetData {

    private Double selectedQuantity;

    private ProductCardData productCardData;

    public ProductCardData getProductCardData() {
        return productCardData;
    }

    public void setProductCardData(ProductCardData productCardData) {
        this.productCardData = productCardData;
    }

    public Double getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(Double selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }

    public boolean compareOnlyNotNullFields(WithdrawalOrderCardData orderCardData) {
        if (orderCardData == null || orderCardData.getProductCardData() == null)
            return false;
        return getProductCardData().compareOnlyNotNullFields(orderCardData.getProductCardData()) &&
                equalsIfRightNotNull(selectedQuantity, orderCardData.getSelectedQuantity());
    }
}
