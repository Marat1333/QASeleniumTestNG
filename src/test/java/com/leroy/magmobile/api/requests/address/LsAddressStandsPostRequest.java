package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/lsAddress/stands")
public class LsAddressStandsPostRequest extends CommonLegoRequest<LsAddressStandsPostRequest> {

    public LsAddressStandsPostRequest setAlleyId(Integer val) {
        return queryParam("alleyId", val);
    }
}
