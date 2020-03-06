package com.leroy.umbrella_extension.magmobile;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.SessionData;
import com.leroy.umbrella_extension.common.LegoBaseClient;
import com.leroy.umbrella_extension.magmobile.data.CartData;
import com.leroy.umbrella_extension.magmobile.data.ProductItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.estimate.EstimateData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ServiceOrderData;
import com.leroy.umbrella_extension.magmobile.data.sales.DiscountData;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentListResponse;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentResponseData;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferSalesDocData;
import com.leroy.umbrella_extension.magmobile.requests.*;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.GetSalesDocDiscount;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.PutSalesDocParametersUpdate;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.products.GetSalesDocProducts;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.products.PostSalesDocProducts;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.products.PutSalesDocProducts;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.search.GetSalesDocSearchV3;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.DeleteSalesDocTransferRequest;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.GetSalesDocTransfer;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.PostSalesDocTransfer;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.PutSalesDocTransferAdd;
import org.json.simple.JSONObject;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.commons.annotations.Dependencies;
import ru.leroymerlin.qa.core.commons.enums.Application;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
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
    private Response<SalesDocumentResponseData> createSalesDocProducts(
            SessionData sessionData, SalesDocumentResponseData salesDocData) {
        PostSalesDocProducts params = new PostSalesDocProducts();
        params.setShopId(sessionData.getUserShopId())
                .setAccessToken(sessionData.getAccessToken());
        if (sessionData.getRegionId() != null)
            params.setRegionId(sessionData.getRegionId());
        params.setSalesDocumentData(salesDocData);
        return execute(params
                .build(gatewayUrl), SalesDocumentResponseData.class);
    }

    public Response<SalesDocumentResponseData> createSalesDocProducts(
            SessionData sessionData, List<ProductOrderData> products, List<ServiceOrderData> services) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setProducts(products);
        salesDocumentResponseData.setServices(services);
        return createSalesDocProducts(sessionData, salesDocumentResponseData);
    }

    public Response<SalesDocumentResponseData> createSalesDocProducts(
            SessionData sessionData, ProductOrderData... productOrderDataArray) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setProducts(Arrays.asList(productOrderDataArray));
        return createSalesDocProducts(sessionData, salesDocumentResponseData);
    }

    public Response<SalesDocumentResponseData> createSalesDocProducts(
            SessionData sessionData, ServiceOrderData... serviceOrderDataArray) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setServices(Arrays.asList(serviceOrderDataArray));
        return createSalesDocProducts(sessionData, salesDocumentResponseData);
    }

    // Lego_Salesdoc_Products_Update
    private Response<SalesDocumentResponseData> updateSalesDocProducts(SessionData sessionData, String fullDocId,
                                                                       SalesDocumentResponseData salesDocData) {
        PutSalesDocProducts params = new PutSalesDocProducts();
        params.setFullDocId(fullDocId);
        params.setSalesDocumentData(salesDocData);
        params.setShopId(sessionData.getUserShopId())
                .setAccessToken(sessionData.getAccessToken());
        if (sessionData.getRegionId() != null)
            params.setRegionId(sessionData.getRegionId());
        return execute(params
                .build(gatewayUrl), SalesDocumentResponseData.class);
    }

    public Response<SalesDocumentResponseData> updateSalesDocProducts(SessionData sessionData, String fullDocId,
                                                                      ProductOrderData... productOrderData) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setProducts(Arrays.asList(productOrderData));
        return updateSalesDocProducts(sessionData, fullDocId, salesDocumentResponseData);
    }

    public Response<SalesDocumentResponseData> updateSalesDocProducts(SessionData sessionData, String fullDocId,
                                                                      ServiceOrderData... serviceOrderData) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setServices(Arrays.asList(serviceOrderData));
        return updateSalesDocProducts(sessionData, fullDocId, salesDocumentResponseData);
    }

    public Response<SalesDocumentResponseData> updateSalesDocProducts(
            SessionData sessionData, String fullDocId,
            List<ProductOrderData> products, List<ServiceOrderData> services) {
        SalesDocumentResponseData salesDocumentResponseData = new SalesDocumentResponseData();
        salesDocumentResponseData.setServices(services);
        salesDocumentResponseData.setProducts(products);
        return updateSalesDocProducts(sessionData, fullDocId, salesDocumentResponseData);
    }

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
        return execute(new LegoOrderWorkflowPut()
                .setOrderId(orderId)
                .setUserLdap(userLdap)
                .jsonBody(jsonObject)
                .build(gatewayUrl), JSONObject.class);
    }

    // Discount

    public Response<DiscountData> getSalesDocDiscount(GetSalesDocDiscount params) {
        return execute(params.build(gatewayUrl), DiscountData.class);
    }

    // Lego SalesDoc Transfer
    public Response<TransferSalesDocData> createSalesDocTransfer(
            SessionData sessionData, TransferSalesDocData transferSalesDocData) {
        PostSalesDocTransfer params = new PostSalesDocTransfer();
        params.setLdap(sessionData.getUserLdap());
        params.jsonBody(transferSalesDocData);
        return execute(params.build(gatewayUrl), TransferSalesDocData.class);
    }

    public Response<TransferSalesDocData> addProductsIntoSalesDocTransfer(
            SessionData sessionData, String taskId, List<TransferProductOrderData> productDataList) {
        PutSalesDocTransferAdd params = new PutSalesDocTransferAdd();
        params.setLdap(sessionData.getUserLdap());
        params.setTaskId(taskId);
        params.setShopId(sessionData.getUserShopId());

        TransferSalesDocData transferSalesDocData = new TransferSalesDocData();
        transferSalesDocData.setProducts(productDataList);
        params.jsonBody(transferSalesDocData);
        return execute(params.build(gatewayUrl), TransferSalesDocData.class);
    }

    public Response<TransferSalesDocData> getTransferSalesDoc(SessionData sessionData, String taskId) {
        GetSalesDocTransfer request = new GetSalesDocTransfer();
        request.setTaskId(taskId);
        request.setLdap(sessionData.getUserLdap());
        return execute(request.build(gatewayUrl), TransferSalesDocData.class);
    }

    public Response<JsonNode> deleteTransferSalesDoc(SessionData sessionData, String taskId) {
        DeleteSalesDocTransferRequest request = new DeleteSalesDocTransferRequest();
        request.setTaskId(taskId);
        return execute(request.build(gatewayUrl), JsonNode.class);
    }

    public Response<TransferSalesDocData> addProductsIntoSalesDocTransfer(SessionData sessionData,
                                                                          String taskId, TransferProductOrderData productData) {
        return addProductsIntoSalesDocTransfer(sessionData, taskId, Collections.singletonList(productData));
    }

    @PostConstruct
    private void init() {
        gatewayUrl = params.getProperty("mashuper.magmobile.url");
    }
}
