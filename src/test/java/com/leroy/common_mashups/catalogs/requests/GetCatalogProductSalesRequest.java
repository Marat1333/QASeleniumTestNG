package com.leroy.common_mashups.catalogs.requests;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/product/{lmCode}/sales")
public class GetCatalogProductSalesRequest extends CommonLegoRequest<GetCatalogProductSalesRequest> {

    public GetCatalogProductSalesRequest setLmCode(String value) {
        return pathParam("lmCode", value);
    }
}
