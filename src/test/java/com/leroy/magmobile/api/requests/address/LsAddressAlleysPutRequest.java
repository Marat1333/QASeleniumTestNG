package com.leroy.magmobile.api.requests.address;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/lsAddress/alleys")
public class LsAddressAlleysPutRequest extends CommonLegoRequest<LsAddressAlleysPutRequest> {

    public LsAddressAlleysPutRequest setAlleyId(Integer val) {
        return queryParam("id", val);
    }

    public LsAddressAlleysPutRequest setCode(String val){
        return queryParam("code", val);
    }
}
