package com.leroy.umbrella_extension.magmobile;

import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.SessionData;
import com.leroy.umbrella_extension.common.LegoBaseClient;
import com.leroy.umbrella_extension.magmobile.data.CartData;
import com.leroy.umbrella_extension.magmobile.data.ProductItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.estimate.EstimateData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderDataList;
import com.leroy.umbrella_extension.magmobile.data.estimate.ServiceOrderDataList;
import com.leroy.umbrella_extension.magmobile.data.sales.DiscountData;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentListResponse;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentResponseData;
import com.leroy.umbrella_extension.magmobile.requests.*;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.*;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.products.GetSalesDocProducts;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.products.PostSalesDocProducts;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.products.PutSalesDocProducts;
import org.json.simple.JSONObject;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.commons.annotations.Dependencies;
import ru.leroymerlin.qa.core.commons.enums.Application;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Dependencies(bricks = Application.MAGMOBILE)
public class MagMobileClient extends LegoBaseClient {

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
    public Response<SalesDocumentResponseData> cancelSalesDoc(SessionData sessionData, String fullDocId) {
        PutSalesDocParametersUpdate params = new PutSalesDocParametersUpdate();
        params.setAccessToken(sessionData.getAccessToken())
                .setLdap(sessionData.getUserLdap())
                .setShopId(sessionData.getUserShopId())
                .setFullDocId(fullDocId)
                .setStatus(SalesDocumentsConst.States.CANCELLED.getApiVal());
        return execute(params.build(gatewayUrl), SalesDocumentResponseData.class);
    }

    // Lego_Salesdoc_Products
    public Response<SalesDocumentResponseData> getSalesDocProductsByFullDocId(String fullDocId) {
        return execute(new GetSalesDocProducts()
                .setFullDocId(fullDocId).build(gatewayUrl), SalesDocumentResponseData.class);
    }

    // Lego_Salesdoc_Products_Create
    public Response<SalesDocumentResponseData> createSalesDocProducts(
            SessionData sessionData, ProductOrderDataList products, ServiceOrderDataList services) {
        PostSalesDocProducts params = new PostSalesDocProducts();
        params.setShopId(sessionData.getUserShopId())
                .setAccessToken(sessionData.getAccessToken());
        if (sessionData.getRegionId() != null)
            params.setRegionId(sessionData.getRegionId());
        if (products != null)
            params.setProducts(products);
        if (services != null)
            params.setServices(services);
        return execute(params
                .build(gatewayUrl), SalesDocumentResponseData.class);
    }

    public Response<SalesDocumentResponseData> createSalesDocProducts(
            SessionData sessionData, ProductOrderDataList products) {
        return createSalesDocProducts(sessionData, products, null);
    }

    public Response<SalesDocumentResponseData> createSalesDocProducts(
            SessionData sessionData, ServiceOrderDataList services) {
        return createSalesDocProducts(sessionData, null, services);
    }

    // Lego_Salesdoc_Products_Update
    public Response<SalesDocumentResponseData> updateSalesDocProducts(SessionData sessionData, String fullDocId, ProductOrderDataList products) {
        return updateSalesDocProducts(sessionData, fullDocId, products, null);
    }

    public Response<SalesDocumentResponseData> updateSalesDocProducts(SessionData sessionData, String fullDocId, ServiceOrderDataList services) {
        return updateSalesDocProducts(sessionData, fullDocId, null, services);
    }

    public Response<SalesDocumentResponseData> updateSalesDocProducts(SessionData sessionData, String fullDocId,
                                                                      ProductOrderDataList products,
                                                                      ServiceOrderDataList services) {
        PutSalesDocProducts params = new PutSalesDocProducts();
        params.setFullDocId(fullDocId);
        if (products != null)
            params.setProducts(products);
        if (services != null)
            params.setServices(services);
        params.setShopId(sessionData.getUserShopId())
                .setAccessToken(sessionData.getAccessToken());
        if (sessionData.getRegionId() != null)
            params.setRegionId(sessionData.getRegionId());
        return execute(params
                .build(gatewayUrl), SalesDocumentResponseData.class);
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
