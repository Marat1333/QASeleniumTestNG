package com.leroy.magmobile.api.data.sales;

import lombok.Data;

import java.util.List;

@Data
public class DiscountData {

    private Double maxDiscount;
    List<DiscountReasonData> reasons;

}
