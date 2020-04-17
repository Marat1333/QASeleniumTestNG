package com.leroy.magmobile.api.requests.catalog;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/shops")
public class GetCatalogShops extends GetCatalogProduct {
    public GetCatalogShops setShopId(String... shopIdArray) {
        return (GetCatalogShops) queryParam("shopId", String.join(",", shopIdArray));
    }

    public GetCatalogShops setShopId(String val) {
        return (GetCatalogShops) super.setShopId(val);
    }

    public GetCatalogShops setLmCode(String value) {
        return (GetCatalogShops) super.setLmCode(value);
    }
}
