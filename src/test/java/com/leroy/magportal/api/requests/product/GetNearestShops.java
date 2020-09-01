package com.leroy.magportal.api.requests.product;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/catalog/product/nearestShops")
public class GetNearestShops extends CommonLegoRequest<GetNearestShops> {
    public GetNearestShops setLmCode(String lmCode) {
        return queryParam("lmCode", lmCode);
    }
}
