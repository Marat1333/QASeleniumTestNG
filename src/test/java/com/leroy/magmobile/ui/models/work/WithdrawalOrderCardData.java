package com.leroy.magmobile.ui.models.work;

import com.leroy.magmobile.ui.models.CardWidgetData;
import com.leroy.magmobile.ui.models.search.ProductCardData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class WithdrawalOrderCardData extends CardWidgetData {

    private Double selectedQuantity;

    private ProductCardData productCardData;

    public boolean compareOnlyNotNullFields(WithdrawalOrderCardData orderCardData) {
        if (orderCardData == null || orderCardData.getProductCardData() == null)
            return false;
        return getProductCardData().compareOnlyNotNullFields(orderCardData.getProductCardData()) &&
                equalsIfRightNotNull(selectedQuantity, orderCardData.getSelectedQuantity());
    }
}
