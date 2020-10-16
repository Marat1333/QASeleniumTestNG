package com.leroy.magportal.api.requests.picking;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/picking/storageLocations")
public class PickingLocationGetRequest extends
        CommonSearchRequestBuilder<PickingLocationGetRequest> {

}