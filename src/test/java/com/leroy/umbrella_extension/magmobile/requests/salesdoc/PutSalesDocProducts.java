package com.leroy.umbrella_extension.magmobile.requests.salesdoc;

import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderDataList;
import com.leroy.umbrella_extension.magmobile.data.estimate.ServiceOrderDataList;
import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "PUT", path = "/salesdoc/products")
public class PutSalesDocProducts extends RequestBuilder<PutSalesDocProducts> {

    // Query params
    public PutSalesDocProducts setLdap(String val) {
        return header("ldap", val);
    }

    public PutSalesDocProducts setFullDocId(String val) {
        return queryParam("fullDocId", val);
    }

    public PutSalesDocProducts setShopId(String val) {
        return queryParam("shopId", val);
    }

    public PutSalesDocProducts setRegionIdj(String val) {
        return queryParam("regionId", val);
    }

    // Body params

    public PutSalesDocProducts setProducts(ProductOrderDataList products) {
        return jsonBody(products);
    }

    public PutSalesDocProducts setServices(ServiceOrderDataList services) {
        return jsonBody(services);
    }

}
