package com.leroy.common_mashups.catalogs.requests.obsolete;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/supplier")
public class GetCatalogSupplierRequestObsolete extends GetCatalogProduct {
    public GetCatalogSupplierRequestObsolete setLmCode(String value) {
        return (GetCatalogSupplierRequestObsolete) super.setLmCode(value);
    }

    public GetCatalogSupplierRequestObsolete setShopId(String shopId) {
        return (GetCatalogSupplierRequestObsolete) super.setShopId(shopId);
    }
}
