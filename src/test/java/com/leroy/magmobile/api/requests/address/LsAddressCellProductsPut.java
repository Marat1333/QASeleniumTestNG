package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/lsAddress/cell/products")
public class LsAddressCellProductsPut extends CommonLegoRequest<LsAddressCellProductsPut> {
    public LsAddressCellProductsPut setCellId(String val) {
        return queryParam("cellId", val);
    }

    public LsAddressCellProductsPut setLmCode(String val) {
        return queryParam("lmCode", val);
    }
}