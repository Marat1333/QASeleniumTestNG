package com.leroy.magmobile.api.requests.catalog;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/product/sales")
public class GetCatalogProductSales extends CommonLegoRequest<GetCatalogProductSales> {

    public GetCatalogProductSales setLmCode(String value) {
        return queryParam("lmCode", value);
    }
}
