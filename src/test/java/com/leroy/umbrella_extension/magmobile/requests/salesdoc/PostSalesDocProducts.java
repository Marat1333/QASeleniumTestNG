package com.leroy.umbrella_extension.magmobile.requests.salesdoc;

import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderDataList;
import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/salesdoc/products")
public class PostSalesDocProducts extends RequestBuilder<PostSalesDocProducts> {

    public PostSalesDocProducts setShopId(String val) {
        return queryParam("shopId", val);
    }

    public PostSalesDocProducts setRegionId(String val) {
        return queryParam("regionId", val);
    }

    public PostSalesDocProducts setProducts(ProductOrderDataList products) {
        return jsonBody(products);
    }

}
