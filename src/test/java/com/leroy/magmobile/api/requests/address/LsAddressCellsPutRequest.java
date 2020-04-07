package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/lsAddress/cells")
public class LsAddressCellsPutRequest extends CommonLegoRequest<LsAddressCellsPutRequest> {

    public LsAddressCellsPutRequest setStandId(Integer val) {
        return queryParam("standId", val);
    }
}
