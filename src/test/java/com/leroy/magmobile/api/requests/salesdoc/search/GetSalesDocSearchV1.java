package com.leroy.magmobile.api.requests.salesdoc.search;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/salesdoc/search")
public class GetSalesDocSearchV1 extends CommonSearchRequestBuilder<GetSalesDocSearchV3> {
}
