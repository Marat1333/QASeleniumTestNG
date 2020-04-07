package com.leroy.magmobile.api.requests.address;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/lsAddress/cells")
public class LsAddressCellsRequest extends BaseLsAddressRequest<LsAddressCellsRequest> {

    public LsAddressCellsRequest setStandId(Integer val) {
        return queryParam("standId", val);
    }
}
