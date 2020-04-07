package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/lsAddress/cells")
public class LsAddressCellsRequest extends CommonLegoRequest<LsAddressCellsRequest> {

    public LsAddressCellsRequest setStandId(Integer val) {
        return queryParam("standId", val);
    }
}
