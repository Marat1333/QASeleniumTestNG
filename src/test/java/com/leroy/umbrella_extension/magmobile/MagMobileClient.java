package com.leroy.umbrella_extension.magmobile;

import com.leroy.magmobile.api.SessionData;
import com.leroy.umbrella_extension.magmobile.data.CartData;
import com.leroy.umbrella_extension.magmobile.data.ProductItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.estimate.EstimateData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderDataList;
import com.leroy.umbrella_extension.magmobile.data.estimate.ServiceOrderDataList;
import com.leroy.umbrella_extension.magmobile.data.sales.DiscountData;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentListResponse;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentResponse;
import com.leroy.umbrella_extension.magmobile.requests.*;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.*;
import org.json.simple.JSONObject;
import ru.leroymerlin.qa.core.clients.base.BaseClient;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.commons.annotations.Dependencies;
import ru.leroymerlin.qa.core.commons.enums.Application;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Dependencies(bricks = Application.MAGMOBILE)
public class MagMobileClient extends BaseClient {

    private String gatewayUrl;

    // Searching
    public Response<ProductItemListResponse> searchProductsBy(GetCatalogSearch params) {
        return execute(params.build(gatewayUrl), ProductItemListResponse.class);
    }

    public Response<ServiceItemListResponse> searchServicesBy(GetCatalogServicesSearch params) {
        return execute(params.build(gatewayUrl), ServiceItemListResponse.class);
    }

    // Estimates
    public Response<EstimateData> createEstimate(String token, String shopId,
                                                 List<ProductOrderData> productOrderDataList) {
        EstimateData estimateData = new EstimateData();
        estimateData.setProducts(productOrderDataList);
        return execute(new EstimatesPost()
                .bearerAuthHeader(token)
                .setShopId(shopId)
                .jsonBody(estimateData).build(gatewayUrl), EstimateData.class);
    }

    public Response<EstimateData> createEstimate(String token, String shopId,
                                                 ProductOrderData productOrderData) {
        return createEstimate(token, shopId, Arrays.asList(productOrderData));
    }

    // Carts (Basket)
    public Response<CartData> createCart(String token, String shopId,
                                         List<ProductOrderData> productOrderDataList) {
        CartData cartData = new CartData();
        cartData.setProducts(productOrderDataList);
        return execute(new CartPOST()
                .bearerAuthHeader(token)
                .setShopId(shopId)
                .jsonBody(cartData).build(gatewayUrl), CartData.class);
    }

    public Response<CartData> createCart(String token, String shopId,
                                         ProductOrderData productOrderData) {
        return createCart(token, shopId, Arrays.asList(productOrderData));
    }

    // ---------  SalesDoc & Orders -------------------- //

    // Lego_Salesdoc_Parameters_Update
    public Response<SalesDocumentResponse> updateSalesDocParameters(PutSalesDocParametersUpdate params) {
        return execute(params.build(gatewayUrl), SalesDocumentResponse.class);
    }

    // Lego_Salesdoc_Products
    public Response<SalesDocumentResponse> getSalesDocProductsByFullDocId(String fullDocId) {
        return execute(new GetSalesDocProducts()
                .setFullDocId(fullDocId).build(gatewayUrl), SalesDocumentResponse.class);
    }

    // Lego_Salesdoc_Products_Create
    public Response<SalesDocumentResponse> createSalesDocProducts(
            SessionData sessionData, ProductOrderDataList products) {
        PostSalesDocProducts params = new PostSalesDocProducts();
        if (sessionData.getRegionId() != null)
            params.setRegionId(sessionData.getRegionId());
        params.setShopId(sessionData.getUserShopId());
        params.setProducts(products);
        return execute(params
                .build(gatewayUrl), SalesDocumentResponse.class);
    }

    // Lego_Salesdoc_Products_Update
    public Response<SalesDocumentResponse> updateSalesDocProducts(ProductOrderDataList products) {
        return execute(new PutSalesDocProducts().setProducts(products)
                .build(gatewayUrl), SalesDocumentResponse.class);
    }

    public Response<SalesDocumentResponse> updateSalesDocProducts(ServiceOrderDataList services) {
        return execute(new PutSalesDocProducts().setServices(services)
                .build(gatewayUrl), SalesDocumentResponse.class);
    }

    public Response<SalesDocumentResponse> updateSalesDocProducts(ProductOrderDataList products,
                                                                  ServiceOrderDataList services) {
        return execute(new PutSalesDocProducts().setServices(services).setProducts(products)
                .build(gatewayUrl), SalesDocumentResponse.class);
    }

    //
    public Response<JSONObject> cancelOrder(String userLdap, String orderId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "cancel-order");
        return execute(new LegoOrderWorkflowPut()
                .setOrderId(orderId)
                .setUserLdap(userLdap)
                .jsonBody(jsonObject)
                .build(gatewayUrl), JSONObject.class);
    }

    public Response<SalesDocumentListResponse> getSalesDocumentsByPinCodeOrDocId(String pinCodeOrDocId) {
        return execute(new SalesDocSearchGET()
                .queryParam("pinCodeOrDocId", pinCodeOrDocId)
                .build(gatewayUrl), SalesDocumentListResponse.class);
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
