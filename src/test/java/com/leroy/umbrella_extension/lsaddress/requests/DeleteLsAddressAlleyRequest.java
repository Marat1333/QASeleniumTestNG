package com.leroy.umbrella_extension.lsaddress.requests;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "DELETE", path = "ls_address_operations/api/Alleys/{alleyId}")
public class DeleteLsAddressAlleyRequest extends RequestBuilder<DeleteLsAddressAlleyRequest> {

    public DeleteLsAddressAlleyRequest setAlleyId(int alleyId) {
        return pathParam("alleyId", alleyId);
    }

}
