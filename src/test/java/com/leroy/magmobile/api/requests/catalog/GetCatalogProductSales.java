package com.leroy.magmobile.api.requests.catalog;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/product/sales")
public class GetCatalogProductSales extends GetCatalogProduct {

    public GetCatalogProductSales setLmCode(String value) {
        return (GetCatalogProductSales) super.setLmCode(value);
    }
}
