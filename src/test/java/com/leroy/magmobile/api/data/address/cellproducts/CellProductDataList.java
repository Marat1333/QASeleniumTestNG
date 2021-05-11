package com.leroy.magmobile.api.data.address.cellproducts;

import lombok.Data;

import java.util.List;

@Data
public class CellProductDataList {
    private int quantity;
    private List<CellProductData> items;
}
