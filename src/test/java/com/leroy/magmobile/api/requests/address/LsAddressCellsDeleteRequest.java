package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "DELETE", path = "/lsAddress/cells")
public class LsAddressCellsDeleteRequest extends CommonLegoRequest<LsAddressCellsDeleteRequest> {

    public LsAddressCellsDeleteRequest setCellId(String val) {
        return queryParam("cellId", val);
    }
}
