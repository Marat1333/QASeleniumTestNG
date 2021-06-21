package com.leroy.magportal.api.requests.picking;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/additionalProductsInfo")
public class PickingTaskGetAdditionalProductsInfo extends
        CommonLegoRequest<PickingTaskGetAdditionalProductsInfo> {

    public PickingTaskGetAdditionalProductsInfo setLmCodes(String val) {
        return queryParam("lmCodes", val);
    }

    public PickingTaskGetAdditionalProductsInfo setShopId(String val) {
        return queryParam("shopId", val);
    }
}