package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "DELETE", path = "/lsAddress/alleys")
public class LsAddressAlleysDeleteRequest extends CommonLegoRequest<LsAddressAlleysDeleteRequest> {
    public LsAddressAlleysDeleteRequest setAlleyId(Integer val) {
        return queryParam("id", val);
    }

    public LsAddressAlleysDeleteRequest setCode(String val) {
        return queryParam("code", val);
    }


}
