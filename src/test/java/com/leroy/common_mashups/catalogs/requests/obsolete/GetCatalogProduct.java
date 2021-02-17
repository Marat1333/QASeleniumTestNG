package com.leroy.common_mashups.catalogs.requests.obsolete;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v3/catalog/product")
public class GetCatalogProduct extends CommonLegoRequest<GetCatalogProduct> {

    public GetCatalogProduct setLmCode(String value) {
        return queryParam("lmCode", value);
    }

    public GetCatalogProduct setExtend(String value) {
        return queryParam("extend", value);
    }

    public GetCatalogProduct setPointOfGiveAway(String value) {
        return queryParam("pointOfGiveAway", value);
    }
}
