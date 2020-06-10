package com.leroy.magportal.api.requests;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/product/similars")
public class GetCatalogProductSimilars extends CommonLegoRequest<GetCatalogProductSimilars> {

    public GetCatalogProductSimilars setLmCode(String lmCode) {
        return queryParam("lmCode", lmCode);
    }
}
