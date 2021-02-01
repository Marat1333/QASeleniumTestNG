package com.leroy.common_mashups.catalogs.requests.obsolete;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/supplier")
public class GetCatalogSupplierRequest extends GetCatalogProduct {
    public GetCatalogSupplierRequest setLmCode(String value) {
        return (GetCatalogSupplierRequest) super.setLmCode(value);
    }

    public GetCatalogSupplierRequest setShopId(String shopId) {
        return (GetCatalogSupplierRequest) super.setShopId(shopId);
    }
}
