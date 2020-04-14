package com.leroy.magmobile.api.requests.catalog;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/supplier")
public class GetCatalogSupplier extends GetCatalogProduct {
    public GetCatalogSupplier setLmCode(String value) {
        return (GetCatalogSupplier)super.setLmCode(value);
    }

    public GetCatalogSupplier setShopId(String shopId){
        return (GetCatalogSupplier) super.setShopId(shopId);
    }
}
