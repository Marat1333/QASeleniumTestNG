package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.requests.salesdoc.cart.*;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.leroy.core.matchers.Matchers.approximatelyEqual;
import static com.leroy.core.matchers.Matchers.isNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CartClient extends MagMobileClient {

    public enum RequestType {
        UPDATE, GET;
    }

    /**
     * ---------- Executable Requests -------------
     **/

    public Response<CartData> sendRequestGet(String cartId) {
        return execute(new CartGet().setCartId(cartId)
                .bearerAuthHeader(sessionData.getAccessToken())
                .setShopId(sessionData.getUserShopId()), CartData.class);
    }

    public Response<CartData> sendRequestCreate(List<CartProductOrderData> productOrderDataList) {
        CartData cartData = new CartData();
        cartData.setProducts(productOrderDataList);
        return execute(new CartPOST()
                .bearerAuthHeader(sessionData.getAccessToken())
                .setShopId(sessionData.getUserShopId())
                .jsonBody(cartData), CartData.class);
    }

    public Response<CartData> sendRequestCreate(
            CartProductOrderData productOrderData) {
        return sendRequestCreate(Collections.singletonList(productOrderData));
    }

    public Response<CartData> addProduct(String cartId, Integer documentVersion,
                                         CartProductOrderData productData) {
        CartUpdateRequest req = new CartUpdateRequest();
        req.setCartId(cartId);
        req.setShopId(sessionData.getUserShopId());
        CartData putDat = new CartData();
        putDat.setDocumentVersion(documentVersion);
        putDat.setProducts(Collections.singletonList(productData));
        req.jsonBody(putDat);
        return execute(req, CartData.class);
    }

    public Response<CartData> confirmQuantity(String cartId, int documentVersion, CartProductOrderData productData) {
        CartConfirmQuantityRequest req = new CartConfirmQuantityRequest();
        req.setCartId(cartId);
        req.setShopId(sessionData.getUserShopId());
        req.setLdap(sessionData.getUserLdap());
        CartData putData = new CartData();
        putData.setDocumentVersion(documentVersion);
        putData.setProducts(Collections.singletonList(productData));
        req.jsonBody(putData);
        return execute(req, CartData.class);
    }

    public Response<CartData> addDiscount(String cartId, int documentVersion, CartProductOrderData productData) {
        CartDiscountRequest req = new CartDiscountRequest();
        req.setShopId(sessionData.getUserShopId());
        req.setLdap(sessionData.getUserLdap());
        req.setCartId(cartId);
        CartData putData = new CartData();
        putData.setDocumentVersion(documentVersion);
        putData.setProducts(Collections.singletonList(productData));
        req.jsonBody(putData);
        return execute(req, CartData.class);
    }

    public Response<CartData> removeItems(String cartId, Integer documentVersion, String lineId) {
        CartItemsRequest req = new CartItemsRequest();
        req.setCartId(cartId);
        req.setDocumentVersion(documentVersion);
        req.setLineId(lineId);
        req.setShopId(sessionData.getUserShopId());
        return execute(req, CartData.class);
    }

    public Response<JsonNode> sendRequestDelete(String cartId, int documentVersion) {
        Map<String, String> body = new HashMap<>();
        body.put("status", SalesDocumentsConst.States.DELETED.getApiVal());
        body.put("documentVersion", String.valueOf(documentVersion));
        return execute(new CartChangeStatusRequest()
                .bearerAuthHeader(sessionData.getAccessToken())
                .setCartId(cartId)
                .formBody(body), JsonNode.class);
    }

    /**
     * ------------  Verifications -----------------
     **/
    public CartData assertThatIsCreatedAndGetData(Response<CartData> response, boolean shortCheck) {
        assertThatResponseIsOk(response);
        CartData data = response.asJson();
        assertThat("fullDocId", data.getFullDocId(), isNumber());
        if (!shortCheck) {
            assertThat("docType", data.getDocType(), is(SalesDocumentsConst.Types.CART.getApiVal()));
            assertThat("salesDocStatus", data.getSalesDocStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
            assertThat("documentType", data.getDocumentType(), is(SalesDocumentsConst.Types.CART.getApiVal()));
            assertThat("status", data.getStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
            assertThat("shopId", data.getShopId(), is(sessionData.getUserShopId()));
            assertThat("cartId", data.getCartId(), is(data.getFullDocId()));
            assertThat("documentVersion", data.getDocumentVersion(), is(1));
            assertThat("groupingId", data.getGroupingId(), not(emptyOrNullString()));

            assertThat("products", data.getProducts(), hasSize(greaterThan(0)));
        }

        return data;
    }

    public CartData assertThatIsCreatedAndGetData(Response<CartData> response) {
        return assertThatIsCreatedAndGetData(response, false);
    }

    private void shortVerifyProducts(
            int i, CartEstimateProductOrderData actualProduct, CartEstimateProductOrderData expectedProduct) {
        assertThat(String.format("Product #%s - lmCode", i + 1),
                actualProduct.getLmCode(), is(expectedProduct.getLmCode()));
        assertThat(String.format("Product #%s - title", i + 1),
                actualProduct.getTitle(), is(expectedProduct.getTitle()));
        assertThat(String.format("Product #%s - Quantity", i + 1),
                actualProduct.getQuantity(), is(expectedProduct.getQuantity()));
        /*assertThat(String.format("Product #%s - Available stock", i + 1),
                actualProduct.getAvailableStock(), is(expectedProduct.getAvailableStock()));*/
        assertThat(String.format("Product #%s - Price", i + 1),
                actualProduct.getPrice(), is(expectedProduct.getPrice()));
        /*assertThat(String.format("Product #%s - PriceUnit", i + 1),
                actualProduct.getPriceUnit(), is(expectedProduct.getPriceUnit()));*/
        assertThat(String.format("Product #%s - BarCode", i + 1),
                actualProduct.getBarCode(), is(expectedProduct.getBarCode()));
        assertThat(String.format("Product #%s - Type", i + 1),
                actualProduct.getType(), is(expectedProduct.getType()));
        assertThat(String.format("Product #%s - TopEm", i + 1),
                actualProduct.getTopEM(), is(expectedProduct.getTopEM()));
    }

    public CartClient assertThatResponseContainsAddedProducts(
            Response<CartData> resp, List<CartEstimateProductOrderData> expectedProducts) {
        assertThatResponseIsOk(resp);
        CartData actualData = resp.asJson();
        for (int i = 0; i < actualData.getProducts().size(); i++) {
            CartEstimateProductOrderData actualProduct = actualData.getProducts().get(i);
            CartEstimateProductOrderData expectedProduct = expectedProducts.get(i);
            shortVerifyProducts(i, actualProduct, expectedProduct);
        }
        return this;
    }

    public CartData assertThatResponseMatches(Response<CartData> resp, CartData expectedData) {
        return assertThatResponseMatches(resp, RequestType.GET, expectedData);
    }

    public CartData assertThatResponseMatches(Response<CartData> resp, RequestType reqType, CartData expectedData) {
        assertThatResponseIsOk(resp);
        CartData actualData = resp.asJson();
        assertThat("FullDocId", actualData.getFullDocId(), equalTo(expectedData.getFullDocId()));
        assertThat("docType", actualData.getDocType(), is(expectedData.getDocType()));
        assertThat("salesDocStatus", actualData.getSalesDocStatus(), is(expectedData.getSalesDocStatus()));
        assertThat("documentType", actualData.getDocumentType(), is(expectedData.getDocumentType()));
        assertThat("status", actualData.getStatus(), is(expectedData.getStatus()));
        assertThat("shopId", actualData.getShopId(), is(expectedData.getShopId()));
        assertThat("cartId", actualData.getCartId(), is(expectedData.getCartId()));
        assertThat("documentVersion", actualData.getDocumentVersion(), is(expectedData.getDocumentVersion()));
        assertThat("groupingId", actualData.getGroupingId(), is(expectedData.getGroupingId()));

        assertThat("products", actualData.getProducts(), hasSize(expectedData.getProducts().size()));

        for (int i = 0; i < actualData.getProducts().size(); i++) {
            CartProductOrderData actualProduct = actualData.getProducts().get(i);
            CartProductOrderData expectedProduct = expectedData.getProducts().get(i);
            shortVerifyProducts(i, actualProduct, expectedProduct);
            assertThat(String.format("Product #%s - priceCategory", i + 1),
                    actualProduct.getBarCode(), not(nullValue()));
            if (!reqType.equals(RequestType.UPDATE)) {
                assertThat(String.format("Product #%s - lineId", i + 1),
                        actualProduct.getLineId(), is(expectedProduct.getLineId()));
            }
            assertThat(String.format("Product #%s - stockAdditionBySalesman", i + 1),
                    actualProduct.getStockAdditionBySalesman(),
                    is(expectedProduct.getStockAdditionBySalesman()));
        }
        return actualData;
    }

    public void assertThatQuantityIsConfirmed(Response<CartData> resp, CartData expectedData) {
        assertThatResponseIsOk(resp);
        CartData actualData = resp.asJson();
        List<CartProductOrderData> actualProductDataList = actualData.getProducts();
        List<CartProductOrderData> expectedProductDataList = expectedData.getProducts();
        assertThat("products size", actualProductDataList, hasSize(expectedProductDataList.size()));
        for (int i = 0; i < actualProductDataList.size(); i++) {
            CartProductOrderData actualProductData = actualProductDataList.get(i);
            CartProductOrderData expectedProductData = expectedProductDataList.get(i);
            shortVerifyProducts(i, actualProductData, expectedProductData);
            assertThat("Product #" + (i + 1) + " lineId", actualProductData.getLineId(),
                    is(expectedProductData.getLineId()));
            //assertThat("Product #" + (i + 1) + " lmCode", actualProductData.getLmCode(),
            //        is(expectedProductData.getLmCode()));
            assertThat("Product #" + (i + 1) + " stockAdditionBySalesman",
                    actualProductData.getStockAdditionBySalesman(),
                    is(expectedProductData.getStockAdditionBySalesman()));
        }

    }

    public void assertThatDiscountAdded(Response<CartData> resp, CartData expectedData) {
        assertThatResponseIsOk(resp);
        CartData actualData = resp.asJson();
        List<CartProductOrderData> actualProductDataList = actualData.getProducts();
        List<CartProductOrderData> expectedProductDataList = expectedData.getProducts();
        assertThat("products size", actualProductDataList, hasSize(expectedProductDataList.size()));
        for (int i = 0; i < actualProductDataList.size(); i++) {
            CartProductOrderData actualProductData = actualProductDataList.get(i);
            CartProductOrderData expectedProductData = expectedProductDataList.get(i);
            shortVerifyProducts(i, actualProductData, expectedProductData);
            assertThat("Product #" + (i + 1) + " lineId", actualProductData.getLineId(),
                    is(expectedProductData.getLineId()));
            //assertThat("Product #" + (i + 1) + " lmCode", actualProductData.getLmCode(),
            //        is(expectedProductData.getLmCode()));
            assertThat("Product #" + (i + 1) + " Discount.type",
                    actualProductData.getDiscount().getType(),
                    is(expectedProductData.getDiscount().getType()));
            assertThat("Product #" + (i + 1) + " Discount.typeValue",
                    actualProductData.getDiscount().getTypeValue(),
                    is(expectedProductData.getDiscount().getTypeValue()));
            assertThat("Product #" + (i + 1) + " Discount.actor",
                    actualProductData.getDiscount().getActor(),
                    is(sessionData.getUserLdap()));
            assertThat("Product #" + (i + 1) + " Discount.updated",
                    actualProductData.getDiscount().getUpdated(),
                    approximatelyEqual(LocalDateTime.now()));
            assertThat("Product #" + (i + 1) + " Discount.reason",
                    actualProductData.getDiscount().getReason(),
                    is(expectedProductData.getDiscount().getReason()));
        }
    }

    public void assertThatResponseChangeStatusIsOk(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        assertThat("result", resp.asJson().get("result").asText(), is("OK"));
    }

}
