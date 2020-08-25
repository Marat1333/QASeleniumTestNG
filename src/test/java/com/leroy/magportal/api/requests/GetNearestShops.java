package com.leroy.magportal.api.requests;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/productsearch/v1/products/{lmCode}/nearest-shops")
public class GetNearestShops extends CommonLegoRequest<GetNearestShops> {
    public GetNearestShops setLmCode(String lmCode) {
        return pathParam("lmCode", lmCode);
    }
}
/*@Method(value = "GET", path = "/catalog/product/nearestShops")
public class GetNearestShops extends CommonLegoRequest<GetNearestShops> {
    public GetNearestShops setLmCode(String lmCode) {
        return queryParam("lmCode", lmCode);
    }
}*/
