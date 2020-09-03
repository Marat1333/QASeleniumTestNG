package com.leroy.magportal.api.requests.product;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v4/catalog/product")
public class GetCatalogProduct extends CommonLegoRequest<GetCatalogProduct> {

    public GetCatalogProduct setLmCode(String lmCode) {
        return queryParam("lmCode", lmCode);
    }
}
