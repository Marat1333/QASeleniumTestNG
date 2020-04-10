package com.leroy.magmobile.api.data.ruptures;

import lombok.Data;

@Data
public class ReqRuptureSessionData {
    private Integer sessionId;
    private Integer storeId;
    private Integer departmentId;
    private Integer shopId;
    private RuptureProductData product;
}
