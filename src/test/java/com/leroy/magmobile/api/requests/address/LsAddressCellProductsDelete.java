package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "DELETE", path = "/lsAddress/cell/products")
public class LsAddressCellProductsDelete extends CommonLegoRequest<LsAddressCellProductsDelete> {
    public LsAddressCellProductsDelete setCellId(String val) {
        return queryParam("cellId", val);
    }

    public LsAddressCellProductsDelete setLmCode(String val) {
        return queryParam("lmCode", val);
    }
}