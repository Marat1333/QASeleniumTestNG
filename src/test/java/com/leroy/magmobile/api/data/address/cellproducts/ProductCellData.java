package com.leroy.magmobile.api.data.address.cellproducts;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductCellData {
    private Integer quantity;
    private String id;
    private String code;
    private String shelf;
    private Integer position;
    private Integer type;
    private Integer standId;
}
