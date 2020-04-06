package com.leroy.magmobile.api.requests.address;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/lsAddress/stands")
public class LsAddressStandsRequest extends BaseLsAddressRequest<LsAddressStandsRequest> {

    public LsAddressStandsRequest setAlleyId(Integer val) {
        return queryParam("alleyId", val);
    }
}
