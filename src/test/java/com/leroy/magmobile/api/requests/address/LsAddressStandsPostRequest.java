package com.leroy.magmobile.api.requests.address;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/lsAddress/stands")
public class LsAddressStandsPostRequest extends BaseLsAddressRequest<LsAddressStandsPostRequest> {

    public LsAddressStandsPostRequest setAlleyId(Integer val) {
        return queryParam("alleyId", val);
    }
}
