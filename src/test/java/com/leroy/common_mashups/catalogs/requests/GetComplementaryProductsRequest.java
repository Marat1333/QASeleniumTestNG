package com.leroy.common_mashups.catalogs.requests;

import com.leroy.common_mashups.catalogs.clients.CatalogProductClient;
import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/products/{lmCode}/complements")
public class GetComplementaryProductsRequest extends CommonLegoRequest<GetComplementaryProductsRequest> {
    public GetComplementaryProductsRequest setLmCode(String lmCode) {
        return pathParam("lmCode", lmCode);
    }

    public GetComplementaryProductsRequest setShopId(String shopId) {
        return queryParam("shopId", shopId);
    }

    public GetComplementaryProductsRequest setExtend(CatalogProductClient.Extend extend) {
        return queryParam("extend", extend.toString());
    }
}
