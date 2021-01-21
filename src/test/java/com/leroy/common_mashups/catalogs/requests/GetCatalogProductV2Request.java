package com.leroy.common_mashups.catalogs.requests;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v2/product")
public class GetCatalogProductV2Request extends CommonLegoRequest<GetCatalogProductV2Request> {

    public GetCatalogProductV2Request setLmCode(String lmCode) {
        return queryParam("lmCode", lmCode);
    }
}
