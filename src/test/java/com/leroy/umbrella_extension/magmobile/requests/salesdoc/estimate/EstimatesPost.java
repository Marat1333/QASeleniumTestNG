package com.leroy.umbrella_extension.magmobile.requests.salesdoc.estimate;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/estimates")
public class EstimatesPost extends RequestBuilder<EstimatesPost> {

    public EstimatesPost setShopId(String val) {
        return header("shopid", val);
    }

}
