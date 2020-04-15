package com.leroy.magmobile.api.data.address.cellproducts;

import lombok.Data;

@Data
public class ReqCellProductData {
    private String newCellId;
    private String lmCode;
    private Integer quantity;
    private String username;
    private String addressType;
    private String code;
}
