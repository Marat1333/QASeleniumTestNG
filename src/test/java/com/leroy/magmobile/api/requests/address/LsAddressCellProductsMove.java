package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/lsAddress/cell/products/move")
public class LsAddressCellProductsMove extends CommonLegoRequest<LsAddressCellProductsMove> {
    public LsAddressCellProductsMove setCellId(String val) {
        return queryParam("cellId", val);
    }
}