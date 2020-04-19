package com.leroy.magmobile.api.requests.kladr;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/kladr")
public class GetKladrRequest extends CommonLegoRequest<GetKladrRequest> {

    public GetKladrRequest setType(String val) {
        return queryParam("type", val);
    }

    public GetKladrRequest setRegionId(String val) {
        return queryParam("regionId", val);
    }

    public GetKladrRequest setQuery(String val) {
        return queryParam("query", val);
    }

    public GetKladrRequest setLimit(int val) {
        return queryParam("limit", val);
    }

}
