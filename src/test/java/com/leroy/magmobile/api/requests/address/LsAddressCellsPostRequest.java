package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/lsAddress/cells")
public class LsAddressCellsPostRequest extends CommonLegoRequest<LsAddressCellsPostRequest> {

    public LsAddressCellsPostRequest setStandId(Integer val) {
        return queryParam("standId", val);
    }
}
