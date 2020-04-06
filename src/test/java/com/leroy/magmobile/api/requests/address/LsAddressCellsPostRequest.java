package com.leroy.magmobile.api.requests.address;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/lsAddress/cells")
public class LsAddressCellsPostRequest extends BaseLsAddressRequest<LsAddressCellsPostRequest> {

    public LsAddressCellsPostRequest setStandId(Integer val) {
        return queryParam("standId", val);
    }
}
