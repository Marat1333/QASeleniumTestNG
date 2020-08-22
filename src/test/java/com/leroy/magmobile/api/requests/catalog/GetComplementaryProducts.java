package com.leroy.magmobile.api.requests.catalog;

import com.leroy.magmobile.api.clients.CatalogProductClient;
import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/complementary-products")
public class GetComplementaryProducts extends CommonLegoRequest<GetComplementaryProducts> {
    public GetComplementaryProducts setLmCode(String lmCode) {
        return queryParam("lmCode", lmCode);
    }

    public GetComplementaryProducts setExtend(CatalogProductClient.Extend extend) {
        return queryParam("extend", extend.toString());
    }
}
