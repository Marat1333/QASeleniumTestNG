package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/lsAddress/cells/search")
public class LsAddressCellSearchRequest extends CommonLegoRequest<LsAddressCellSearchRequest> {
    public LsAddressCellSearchRequest setLmCode(String val) {
        return queryParam("lmCode", val);
    }
}
