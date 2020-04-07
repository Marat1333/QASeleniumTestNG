package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/lsAddress/cell/products")
public class LsAddressCellProductsPostRequest extends CommonLegoRequest<LsAddressCellProductsPostRequest> {

    public LsAddressCellProductsPostRequest setCellId(String val) {
        return queryParam("cellId", val);
    }

}
