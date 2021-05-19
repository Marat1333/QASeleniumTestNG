package com.leroy.magmobile.api.data.address.cellproducts;

import lombok.Data;

import java.util.List;

@Data
public class CellProductDataList {
    private Integer quantity;
    private List<CellProductData> items;
}
