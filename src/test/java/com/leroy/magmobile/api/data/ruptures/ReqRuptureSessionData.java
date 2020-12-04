package com.leroy.magmobile.api.data.ruptures;

import lombok.Data;

@Data
public class ReqRuptureSessionData { //TODO нужно переделать этот класс и убрать из него номер сессии и магазина
    private Integer sessionId;
    private Integer storeId;
    private Integer departmentId;
    private Integer shopId;
    private RuptureProductData product;
}
