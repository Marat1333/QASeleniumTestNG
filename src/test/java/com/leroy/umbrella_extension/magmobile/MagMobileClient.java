package com.leroy.umbrella_extension.magmobile;

import com.leroy.umbrella_extension.magmobile.data.ProductItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemListResponse;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogSearch;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogServicesSearch;
import ru.leroymerlin.qa.core.clients.base.BaseClient;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.commons.annotations.Dependencies;
import ru.leroymerlin.qa.core.commons.enums.Application;

import javax.annotation.PostConstruct;

@Dependencies(bricks = Application.MAGMOBILE)
public class MagMobileClient extends BaseClient {

    private String gatewayUrl;

    public Response<ProductItemListResponse> searchProductsBy(GetCatalogSearch params) {
        return execute(params.build(gatewayUrl), ProductItemListResponse.class);
    }

    public Response<ServiceItemListResponse> searchServicesBy(GetCatalogServicesSearch params) {
        return execute(params.build(gatewayUrl), ServiceItemListResponse.class);
    }

    @PostConstruct
    private void init() {
        gatewayUrl = params.getProperty("mashuper.magmobile.url");
    }
}
