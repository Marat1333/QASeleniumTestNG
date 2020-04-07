package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/lsAddress/stands")
public class LsAddressStandsRequest extends CommonLegoRequest<LsAddressStandsRequest> {

    public LsAddressStandsRequest setAlleyId(Integer val) {
        return queryParam("alleyId", val);
    }
}
