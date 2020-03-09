package com.leroy.umbrella_extension.magmobile;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.SessionData;
import com.leroy.umbrella_extension.common.LegoBaseClient;
import com.leroy.umbrella_extension.magmobile.data.CartData;
import com.leroy.umbrella_extension.magmobile.data.ProductItemDataList;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemDataList;
import com.leroy.umbrella_extension.magmobile.data.estimate.EstimateData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.sales.DiscountData;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentListResponse;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferSalesDocData;
import com.leroy.umbrella_extension.magmobile.requests.*;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.GetSalesDocDiscount;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.search.GetSalesDocSearchV3;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.DeleteSalesDocTransferRequest;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.GetSalesDocTransfer;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.PostSalesDocTransfer;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.PutSalesDocTransferAdd;
import org.json.simple.JSONObject;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;
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

    @PostConstruct
    private void init() {
        gatewayUrl = params.getProperty("mashuper.magmobile.url");
    }
}
