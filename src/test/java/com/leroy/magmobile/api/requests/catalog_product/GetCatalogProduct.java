package com.leroy.magmobile.api.requests.catalog_product;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v3/catalog/product")
public class GetCatalogProduct extends CommonSearchRequestBuilder<GetCatalogProduct> {

    public GetCatalogProduct(){
        queryParam("extend","rating,logistic,inventory");
        queryParam("pointOfGiveAway","SALESFLOOR");
    }

    public GetCatalogProduct setLmCode(String value){
        return queryParam("lmCode", value);
    }
}
