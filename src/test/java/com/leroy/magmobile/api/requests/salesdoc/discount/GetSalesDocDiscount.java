package com.leroy.magmobile.api.requests.salesdoc.discount;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/salesdoc/discount")
public class GetSalesDocDiscount extends CommonLegoRequest<GetSalesDocDiscount> {

    public GetSalesDocDiscount setLmCode(String val) {
        return queryParam("lmCode", val);
    }
}
