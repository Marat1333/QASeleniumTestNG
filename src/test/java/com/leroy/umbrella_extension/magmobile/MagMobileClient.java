package com.leroy.umbrella_extension.magmobile;

import com.leroy.umbrella_extension.common.LegoBaseClient;
import com.leroy.umbrella_extension.magmobile.data.catalog.ProductItemDataList;
import com.leroy.umbrella_extension.magmobile.data.catalog.ServiceItemDataList;
import com.leroy.umbrella_extension.magmobile.data.sales.DiscountData;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentListResponse;
import com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate.EstimateData;
import com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.requests.catalog_search.GetCatalogSearch;
import com.leroy.umbrella_extension.magmobile.requests.catalog_search.GetCatalogServicesSearch;
import com.leroy.umbrella_extension.magmobile.requests.order.OrderWorkflowPut;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.GetSalesDocDiscount;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.estimate.EstimatePost;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.search.GetSalesDocSearchV3;
import org.json.simple.JSONObject;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.commons.annotations.Dependencies;
import ru.leroymerlin.qa.core.commons.enums.Application;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Dependencies(bricks = Application.MAGMOBILE)
public class MagMobileClient extends LegoBaseClient {

    private String gatewayUrl;

    public <J> Response<J> execute(RequestBuilder<?> request, final Class<J> type) {
        return super.execute(request.build(gatewayUrl), type);
    }

    // Searching
    public Response<ProductItemDataList> searchProductsBy(GetCatalogSearch params) {
        return execute(params.build(gatewayUrl), ProductItemDataList.class);
    }

    public Response<ServiceItemDataList> searchServicesBy(GetCatalogServicesSearch params) {
        return execute(params.build(gatewayUrl), ServiceItemDataList.class);
    }

    // Estimates
    public Response<EstimateData> createEstimate(String token, String shopId,
                                                 List<ProductOrderData> productOrderDataList) {
        List<ProductOrderData> filteredProducts = new ArrayList<>();
        for (ProductOrderData prData : productOrderDataList) {
            ProductOrderData filterPrData = new ProductOrderData();
            filterPrData.setQuantity(prData.getQuantity());
            filterPrData.setLmCode(prData.getLmCode());
            filteredProducts.add(filterPrData);
        }
        EstimateData estimateData = new EstimateData();
        estimateData.setProducts(filteredProducts);
        return execute(new EstimatePost()
                .bearerAuthHeader(token)
                .setShopId(shopId)
                .jsonBody(estimateData).build(gatewayUrl), EstimateData.class);
    }

    public Response<EstimateData> createEstimate(String token, String shopId,
                                                 ProductOrderData productOrderData) {
        return createEstimate(token, shopId, Arrays.asList(productOrderData));
    }

    // ---------  SalesDoc & Orders -------------------- //

    // Lego_salesdoc_search
    public Response<SalesDocumentListResponse> searchForSalesDocumentBy(GetSalesDocSearchV3 params) {
        return execute(params
                .build(gatewayUrl), SalesDocumentListResponse.class);
    }

    public Response<SalesDocumentListResponse> getSalesDocumentsByPinCodeOrDocId(String pinCodeOrDocId) {
        return execute(new GetSalesDocSearchV3()
                .queryParam("pinCodeOrDocId", pinCodeOrDocId)
                .build(gatewayUrl), SalesDocumentListResponse.class);
    }

    //
    public Response<JSONObject> cancelOrder(String userLdap, String orderId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "cancel-order");
        return execute(new OrderWorkflowPut()
                .setOrderId(orderId)
                .setUserLdap(userLdap)
                .jsonBody(jsonObject)
                .build(gatewayUrl), JSONObject.class);
    }

    // Discount

    public Response<DiscountData> getSalesDocDiscount(GetSalesDocDiscount params) {
        return execute(params.build(gatewayUrl), DiscountData.class);
    }

    @PostConstruct
    private void init() {
        gatewayUrl = params.getProperty("mashuper.magmobile.url");
    }
}
