package com.leroy.magmobile.ui.models.work;

import com.leroy.magmobile.ui.models.search.ProductCardData;
import lombok.Data;

@Data
public class WithdrawalOrderCardData {

    private Double selectedQuantity;

    private ProductCardData productCardData;
}
