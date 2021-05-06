package com.leroy.common_mashups.catalogs.requests;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/products/{lmCode}/supplier")
public class GetCatalogSupplierRequest extends CommonLegoRequest<GetCatalogSupplierRequest> {

    public GetCatalogSupplierRequest setLmCode(String lmCode) { return pathParam("lmCode", lmCode); }
}
